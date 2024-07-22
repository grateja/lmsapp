package com.vag.lmsapp.app.joborders.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import androidx.lifecycle.*
import com.vag.lmsapp.app.joborders.create.delivery.DeliveryCharge
import com.vag.lmsapp.app.joborders.create.discount.MenuDiscount
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.app.joborders.create.packages.MenuJobOrderPackage
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.model.EnumDiscountApplicable
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.room.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateJobOrderViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository,
    private val paymentRepository: PaymentRepository,
    private val productsRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val packageRepository: JobOrderPackageRepository,
) : ViewModel() {
    sealed class DataState {
        object StateLess: DataState()
        data class SaveSuccess(val jobOrderId: UUID, val customerId: UUID): DataState()
        data class OpenServices(val list: List<MenuServiceItem>?, val item: MenuServiceItem?): DataState()
        data class OpenPackages(val list: List<MenuJobOrderPackage>?, val item: MenuJobOrderPackage?) : DataState()
        data class OpenProducts(val list: List<MenuProductItem>?, val item: MenuProductItem?): DataState()
        data class OpenExtras(val list: List<MenuExtrasItem>?, val item: MenuExtrasItem?): DataState()
        data class OpenDelivery(val deliveryCharge: DeliveryCharge?): DataState()
        data class OpenDiscount(val discount: MenuDiscount?): DataState()
        data class OpenPayment(val paymentId: UUID, val customerId: UUID?) : DataState()
        data class MakePayment(val customerId: UUID) : DataState()
        data class InvalidOperation(val message: String, val errorCode: String? = null): DataState()
        data class RequestExit(val canExit: Boolean, val resultCode: Int, val jobOrderId: UUID?) : DataState()
        data class RequestCancel(val jobOrderId: UUID?) : DataState()
        data class ModifyDateTime(val createdAt: Instant) : DataState()
        data class OpenPictures(val jobOrderId: UUID) : DataState()
        data class PickCustomer(val customerId: UUID?) : DataState()
        data class SearchCustomer(val customerId: UUID?) : DataState()
        data class OpenPrinter(val jobOrderId: UUID) : DataState()
        object ProceedToSaveJO: DataState()
    }

    private val _modified = MutableLiveData(false)
    val modified: LiveData<Boolean> = _modified

    private val _saved = MutableLiveData(false)
    val saved: LiveData<Boolean> = _saved

    private val _deleted = MutableLiveData(false)
    val deleted: LiveData<Boolean> = _deleted

    private val _customerId = MutableLiveData<UUID>()

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    private val _jobOrderId = MutableLiveData<UUID>()
    val createdAt = MutableLiveData<Instant>()
    val jobOrderNumber = MutableLiveData("")
    val currentCustomer = _customerId.switchMap { customerRepository.getCustomerAsLiveData(it) } //MutableLiveData<CustomerMinimal>()
    val deliveryCharge = MutableLiveData<DeliveryCharge?>()
    val jobOrderServices = MutableLiveData<List<MenuServiceItem>>()
    val jobOrderProducts = MutableLiveData<List<MenuProductItem>>()
    val jobOrderExtras = MutableLiveData<List<MenuExtrasItem>>()
    val discount = MutableLiveData<MenuDiscount>()
    private val _paymentId = MutableLiveData<UUID>()

    val payment = _paymentId.switchMap { paymentRepository.getPaymentWithJobOrdersAsLiveData(it) }

    val jobOrderPackages = MutableLiveData<List<MenuJobOrderPackage>>()

    private var _packageServices = listOf<EntityJobOrderService>()
    private var _packageProducts = listOf<EntityJobOrderProduct>()
    private var _packageExtras = listOf<EntityJobOrderExtras>()

    val unpaidJobOrders = _customerId.switchMap { jobOrderRepository.getAllUnpaidByCustomerIdAsLiveData(it) }

    val jobOrderPictures = _jobOrderId.switchMap { jobOrderRepository.getPicturesAsLiveData(it) }

    /** region mediator live data */

    val hasPackages = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = jobOrderPackages.value?.filter { !it.deleted }?.size!! > 0
        }
        addSource(jobOrderPackages) {update()}
    }
    val hasServices = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = jobOrderServices.value?.filter { !it.deleted }?.size!! > 0
        }
        addSource(jobOrderServices) { update() }
    }
    val hasProducts = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = jobOrderProducts.value?.filter { !it.deleted }?.size!! > 0
        }
        addSource(jobOrderProducts) { update() }
    }
    val hasExtras = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = jobOrderExtras.value?.filter { !it.deleted }?.size!! > 0
        }
        addSource(jobOrderExtras) { update() }
    }
    val hasDelivery = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = deliveryCharge.value.let {
                it != null && !it.deleted
            }
        }
        addSource(deliveryCharge) { update() }
    }
    val hasDiscount = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = discount.value.let {
                it != null && !it.deleted
            }
        }
        addSource(discount) { update() }
    }
    val hasAny = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = hasServices.value == true || hasProducts.value == true || hasExtras.value == true || hasDelivery.value == true || hasPackages.value == true
        }
        addSource(hasPackages) { update() }
        addSource(hasServices) { update() }
        addSource(hasProducts) { update() }
        addSource(hasExtras) { update() }
        addSource(hasDelivery) { update() }
    }

    val subtotal = MediatorLiveData<Float>().apply {
        fun update() {
            value = productSubTotal() + serviceSubTotal() + extrasSubTotal() + deliveryFee() + packageSubTotal() // - discountInPeso.value!!
        }
        addSource(jobOrderPackages) {update()}
        addSource(jobOrderServices) {update()}
        addSource(jobOrderProducts) {update()}
        addSource(jobOrderExtras) {update()}
        addSource(deliveryCharge) {update()}
    }
    val discountInPeso = MediatorLiveData<Float>().apply {
        fun update() {
            value = discount.value?.let {
                if(it.deleted) return@let 0f
                var total = 0f
                total += it.calculateDiscount(serviceSubTotal(), EnumDiscountApplicable.WASH_DRY_SERVICES)
                total += it.calculateDiscount(productSubTotal(), EnumDiscountApplicable.PRODUCTS_CHEMICALS)
                total += it.calculateDiscount(extrasSubTotal(), EnumDiscountApplicable.EXTRAS)
                total += it.calculateDiscount(deliveryFee(), EnumDiscountApplicable.DELIVERY)
                maxOf(total, 0f)
            } ?: 0f
        }
        addSource(jobOrderServices) {update()}
        addSource(jobOrderProducts) {update()}
        addSource(jobOrderExtras) {update()}
        addSource(deliveryCharge) {update()}
        addSource(discount) {update()}
    }
    val discountedAmount = MediatorLiveData<Float>().apply {
        fun update() {
            val subtotal = subtotal.value ?: 0f
            val discount = discountInPeso.value ?: 0f
            value = subtotal - discount
        }

        addSource(subtotal) {update()}
        addSource(discountInPeso) {update()}
    }

    val discountApplicable = MediatorLiveData<Boolean>().apply {
        fun update() {
            discount.value?.let {
                value = false
                if(it.deleted) {
                    value = true
                    return
                }
                if(hasAny.value == true && it.applicableTo.find { it == EnumDiscountApplicable.TOTAL_AMOUNT } != null) {
                    value = true
                    return
                }
                if(hasServices.value == true && it.applicableTo.find { it == EnumDiscountApplicable.WASH_DRY_SERVICES } != null) {
                    value = true
                    return
                }
                if(hasProducts.value == true && it.applicableTo.find { it == EnumDiscountApplicable.PRODUCTS_CHEMICALS } != null) {
                    value = true
                    return
                }
                if(hasExtras.value == true && it.applicableTo.find { it == EnumDiscountApplicable.EXTRAS } != null) {
                    value = true
                    return
                }
                if(hasDelivery.value == true && it.applicableTo.find { it == EnumDiscountApplicable.DELIVERY } != null) {
                    value = true
                    return
                }
            }
        }
        addSource(hasAny) {update()}
        addSource(discount) {update()}
    }

    val confirmStr = MediatorLiveData<String>().apply {
        fun update() {
            val amount = discountedAmount.value ?: 0f

             val str = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount))

            value = "$str CONFIRM"
        }
        addSource(discountedAmount) {update()}
    }

    /** endregion mediator live data */

    /** region computed functions */

    private fun packageSubTotal(): Float {
        return jobOrderPackages.value?.filter { !it.deleted }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.totalPrice * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    private fun serviceSubTotal() : Float {
        return jobOrderServices.value?.filter { !it.deleted  }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    private fun productSubTotal() : Float {
        return jobOrderProducts.value?.filter { !it.deleted }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    private fun extrasSubTotal() : Float {
        return jobOrderExtras.value?.filter { !it.deleted }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    private fun deliveryFee() : Float {
        return deliveryCharge.value?.let {
            return if( !it.deleted ) {
                it.price
            } else {
                0f
            }
        } ?: 0f
    }

    /** endregion */

    /** region setter functions */

    private fun prepare(jobOrder: EntityJobOrderWithItems) {
        jobOrder.packages?.let {packages ->
            jobOrderPackages.value = packages.map { joPkg ->
                MenuJobOrderPackage(
                    joPkg.id,
                    joPkg.packageId,
                    joPkg.packageName,
                    null,
                    joPkg.quantity,
                    joPkg.price,
                    joPkg.isVoid,
                    joPkg.deleted).apply {
                        selected = true
                }
            }
        }
        jobOrder.services?.let { services ->
            _packageServices = services.filter { it.jobOrderPackageId != null }

            jobOrderServices.value = services.filter { it.jobOrderPackageId == null }.map { joSvc ->
                MenuServiceItem(
                    joSvc.id,
                    joSvc.serviceId,
                    joSvc.serviceName,
                    joSvc.serviceRef.minutes,
                    joSvc.price,
                    joSvc.discountedPrice,
                    joSvc.serviceRef.serviceType,
                    joSvc.serviceRef.machineType,
                    joSvc.serviceRef.washType,
                    joSvc.quantity,
                    joSvc.used,
                    joSvc.isVoid,
                    joSvc.deleted).apply {
                    selected = true
                }
            }
        }
        jobOrder.extras?.let { extras ->
            _packageExtras = extras.filter { it.jobOrderPackageId != null }
            jobOrderExtras.value = extras.filter { it.jobOrderPackageId == null }.map { joExtras ->
                MenuExtrasItem(
                    joExtras.id,
                    joExtras.extrasId,
                    joExtras.extrasName,
                    joExtras.price,
                    joExtras.discountedPrice,
                    joExtras.category,
                    joExtras.quantity,
                    joExtras.isVoid,
                    joExtras.deleted).apply {
                    selected = true
                }
            }
        }
        jobOrder.products?.let { products ->
            _packageProducts = products.filter { it.jobOrderPackageId != null }

            jobOrderProducts.value = products.filter { it.jobOrderPackageId == null }.map { joPrd ->
                MenuProductItem(
                    joPrd.id,
                    joPrd.productId,
                    joPrd.productName,
                    joPrd.price,
                    joPrd.discountedPrice,
                    joPrd.measureUnit,
                    joPrd.unitPerServe,
                    joPrd.quantity,
                    0f,
                    joPrd.productType,
                    joPrd.isVoid,
                    joPrd.deleted).apply {
                    selected = true
                }
            }
        }
        jobOrder.deliveryCharge?.let { entity ->
            deliveryCharge.value = DeliveryCharge(
                entity.deliveryProfileId,
                entity.vehicle,
                entity.distance,
                entity.deliveryOption,
                entity.price,
                entity.discountedPrice,
                entity.deleted,
            )
        }
        jobOrder.discount?.let { entity ->
            discount.value = MenuDiscount(
                entity.discountId,
                entity.name,
                entity.value,
                entity.applicableTo,
                entity.isVoid,
                entity.deleted,
            )
        }

        _saved.value = true
        _deleted.value = jobOrder.jobOrder.deleted || jobOrder.jobOrder.entityJobOrderVoid != null
    }

    fun loadEmptyJobOrder(customerId: UUID?) {
        viewModelScope.launch {
            _jobOrderId.value = UUID.randomUUID()
            createdAt.value = Instant.now()
            jobOrderNumber.value = jobOrderRepository.getNextJONumber()
            customerId?.let {
                _customerId.value = it
            }
        }
    }

    fun loadByJobOrderId(joId: UUID?) {
        viewModelScope.launch {
            jobOrderRepository.getJobOrderWithItems(joId).let {
                _customerId.value = it?.jobOrder?.customerId
                _jobOrderId.value = it?.jobOrder?.id
                createdAt.value = it?.jobOrder?.createdAt
                jobOrderNumber.value = it?.jobOrder?.jobOrderNumber
                if(it != null) {
                    prepare(it)
                } else {
                    _jobOrderId.value = UUID.randomUUID()
                    createdAt.value = Instant.now()
                    jobOrderNumber.value = jobOrderRepository.getNextJONumber()
                }
            }
        }
    }

    fun loadByCustomer(customerId: UUID) {
        _customerId.value = customerId
        viewModelScope.launch {
            jobOrderRepository.getCurrentJobOrder(customerId)?.let { jobOrderWithItems ->
                _jobOrderId.value = jobOrderWithItems.jobOrder.id
                createdAt.value = jobOrderWithItems.jobOrder.createdAt
                jobOrderNumber.value = jobOrderWithItems.jobOrder.jobOrderNumber
                prepare(jobOrderWithItems)
            }
        }
    }

    fun loadPayment(paymentId: UUID) {
        _paymentId.value = paymentId
    }

    fun setCustomerId(customerId: UUID) {
        _customerId.value = customerId
        _saved.value = false
        _modified.value = true
    }

    fun syncPackages(packages: List<MenuJobOrderPackage>?) {
        viewModelScope.launch {
            val packageServices = _packageServices.map{ it.copy() }.toMutableList()
            val packageProducts = _packageProducts.map{ it.copy() }.toMutableList()
            val packageExtras = _packageExtras.map{ it.copy() }.toMutableList()
            val jobOrderId = _jobOrderId.value

            packages?.onEach { _jobOrderPackage ->
                packageRepository.getPackageServicesByPackageId(_jobOrderPackage.packageRefId).onEach {_packageService ->
                    val packageService = packageServices.find {
                        it.serviceId == _packageService.serviceId && it.jobOrderPackageId == _packageService.packageId
                    }
                    val newQuantity = _jobOrderPackage.quantity * _packageService.quantity

                    if(packageService != null && _jobOrderPackage.deleted) {
                        if(packageService.used > 0) {
                            _dataState.value = DataState.InvalidOperation("Cannot modify package with used services")
                            return@launch
                        } else {
                            packageService.deleted = true
                        }
                    } else if(packageService == null) {
                        packageServices.add(
                            EntityJobOrderService(
                                jobOrderId,
                                _packageService.serviceId,
                                _jobOrderPackage.packageRefId,
                                _packageService.serviceName,
                                _packageService.unitPrice,
                                _packageService.unitPrice,
                                newQuantity,
                                0,
                                false,
                                _packageService.serviceRef,
                                UUID.randomUUID()
                            )
                        )
                    } else {
                        if(packageService.used > newQuantity) {
                            _dataState.value = DataState.InvalidOperation("Cannot modify package with used services")
                            return@launch
                        }
                        packageService.quantity = newQuantity
                        packageService.deleted = false
                    }
                }
                packageRepository.getPackageProductsByPackageId(_jobOrderPackage.packageRefId).onEach { _packageProduct ->
                    val packageProduct = packageProducts.find {
                        it.productId == _packageProduct.productId  && it.jobOrderPackageId == _packageProduct.packageId
                    }
                    val newQuantity = _jobOrderPackage.quantity * _packageProduct.quantity
                    if(packageProduct != null && _jobOrderPackage.deleted) {
                        packageProduct.deleted = true
                    } else if(packageProduct == null) {
                        packageProducts.add(
                            EntityJobOrderProduct(
                                jobOrderId,
                                _packageProduct.productId,
                                _jobOrderPackage.packageRefId,
                                _packageProduct.productName,
                                _packageProduct.unitPrice,
                                _packageProduct.unitPrice,
                                _packageProduct.measureUnit,
                                _packageProduct.unitPerServe,
                                newQuantity,
                                _packageProduct.productType,
                                false,
                                UUID.randomUUID()
                            )
                        )
                    } else {
                        packageProduct.deleted = false
                        packageProduct.quantity = newQuantity
                    }
                }
                packageRepository.getPackageExtrasByPackageId(_jobOrderPackage.packageRefId).onEach { _packageExtra ->
                    val packageExtra = packageExtras.find {
                        it.extrasId == _packageExtra.extrasId  && it.jobOrderPackageId == _packageExtra.packageId
                    }
                    val newQuantity = _jobOrderPackage.quantity * _packageExtra.quantity
                    if(packageExtra != null && _jobOrderPackage.deleted) {
                        packageExtra.deleted = true
                    } else if(packageExtra == null) {
                        packageExtras.add(
                            EntityJobOrderExtras(
                                jobOrderId,
                                _packageExtra.extrasId,
                                _jobOrderPackage.packageRefId,
                                _packageExtra.extrasName,
                                _packageExtra.unitPrice,
                                _packageExtra.unitPrice,
                                newQuantity,
                                _packageExtra.category,
                                false,
                                UUID.randomUUID()
                            )
                        )
                    } else {
                        packageExtra.deleted = false
                        packageExtra.quantity = newQuantity
                    }
                }

                if(_jobOrderPackage.deleted) {
                    packageServices.onEach {
                        it.deleted = true
                    }
                    packageProducts.onEach {
                        it.deleted = true
                    }
                    packageExtras.onEach {
                        it.deleted = true
                    }
                }
            }

            _packageServices = packageServices
            _packageProducts = packageProducts
            _packageExtras = packageExtras

            jobOrderPackages.value = packages ?: emptyList()
            _saved.value = false
            _modified.value = true
        }
    }

    private fun refreshDiscount() {
        val discount = discount.value
        val serviceDiscount = discount?.takeIf { !it.deleted && it.applicableTo.any {
            it in listOf(EnumDiscountApplicable.WASH_DRY_SERVICES,
                EnumDiscountApplicable.TOTAL_AMOUNT)} }?.value ?: 0f

        val productDiscount = discount?.takeIf { !it.deleted && it.applicableTo.any {
            it in listOf(EnumDiscountApplicable.PRODUCTS_CHEMICALS,
                EnumDiscountApplicable.TOTAL_AMOUNT) } }?.value ?: 0f

        val extrasDiscount = discount?.takeIf { !it.deleted && it.applicableTo.any {
            it in listOf(EnumDiscountApplicable.EXTRAS,
                EnumDiscountApplicable.TOTAL_AMOUNT) } }?.value ?: 0f

        val deliveryDiscount = discount?.takeIf { !it.deleted && it.applicableTo.any {
            it in listOf(EnumDiscountApplicable.DELIVERY,
                EnumDiscountApplicable.TOTAL_AMOUNT) } }?.value ?: 0f

        jobOrderServices.value = jobOrderServices.value?.map { serviceItem ->
            serviceItem.copy(
                discountedPrice = serviceItem.price - (serviceItem.price * (serviceDiscount / 100))
            )
        } ?: emptyList()
        jobOrderProducts.value = jobOrderProducts.value?.map { productItem ->
            productItem.copy(
                discountedPrice = productItem.price - (productItem.price * (productDiscount / 100))
            )
        } ?: emptyList()
        jobOrderExtras.value = jobOrderExtras.value?.map { extrasItem ->
            extrasItem.copy(
                discountedPrice = extrasItem.price - (extrasItem.price * (extrasDiscount / 100))
            )
        } ?: emptyList()
        deliveryCharge.value = deliveryCharge.value?.apply {
            discountedPrice = price - (price * (deliveryDiscount / 100))
        }
    }

    fun syncServices(services: List<MenuServiceItem>?) {
        services?.let {
            jobOrderServices.value = it.toList()
            _saved.value = false
            _modified.value = true
        }
        refreshDiscount()
    }

    fun syncProducts(products: List<MenuProductItem>?) {
        jobOrderProducts.value = products?.toList()
        _saved.value = false
        _modified.value = true
        refreshDiscount()
    }

    fun syncExtras(extrasItems: List<MenuExtrasItem>?) {
        jobOrderExtras.value = extrasItems?.toList()
        _saved.value = false
        _modified.value = true
        refreshDiscount()
    }

    fun setDeliveryCharge(deliveryCharge: DeliveryCharge?) {
        if(deliveryCharge == null) {
            this.deliveryCharge.value = this.deliveryCharge.value?.apply {
                 this.deleted = true
            }
        }
        this.deliveryCharge.value = deliveryCharge
        _saved.value = false
        _modified.value = true
        refreshDiscount()
    }

    fun applyDiscount(discount: MenuDiscount?) {
        if(discount == null) {
            this.discount.value = this.discount.value?.apply {
                this.deleted = true
            }
        } else {
            this.discount.value = discount
        }
        _saved.value = false
        _modified.value = true
        refreshDiscount()
    }

    fun removeService(id: UUID?) {
        jobOrderServices.value?.apply {
            val found = this.find { it.serviceRefId == id }

            if(found != null) {
                if(found.used > 0) {
                    _dataState.value = DataState.InvalidOperation("Cannot remove used service")
                    return@apply
                }

                if(found.joServiceItemId != null) {
                    found.deleted = true
                    jobOrderServices.value = this
                } else {
                    jobOrderServices.value = this.filter { it.serviceRefId != id }
                }
                _saved.value = false
                _modified.value = true
            }
        }
    }

    fun removeProduct(id: UUID?) {
        jobOrderProducts.value?.apply {
            val found = this.find { it.productRefId == id }

            if(found != null) {
                if(found.joProductItemId != null) {
                    found.deleted = true
                    jobOrderProducts.value = this
                } else {
                    jobOrderProducts.value = this.filter { it.productRefId != id }
                }
                _saved.value = false
                _modified.value = true
            }
        }
    }

    fun removeExtras(id: UUID?) {
        jobOrderExtras.value?.apply {
            val found = this.find { it.extrasRefId == id }

            if(found != null) {
                if(found.joExtrasItemId != null) {
                    found.deleted = true
                    jobOrderExtras.value = this
                } else {
                    jobOrderExtras.value = this.filter { it.extrasRefId != id }
                }
                _saved.value = false
                _modified.value = true
            }
        }
    }

    fun applyDateTime(instant: Instant) {
        if(instant.isAfter(Instant.now())) {
            _dataState.value = DataState.InvalidOperation("Date created must not be after today")
        } else {
            createdAt.value = instant
            _saved.value = false
            _modified.value = true
        }
    }

    /** endregion */

    /** region navigation */
    fun requestModifyDateTime() {
        createdAt.value?.let {
            _dataState.value = DataState.ModifyDateTime(it)
        }
    }

    fun openPackages(itemPreset: MenuJobOrderPackage?) {
        jobOrderPackages.value.let {
            _dataState.value = DataState.OpenPackages(it, itemPreset)
        }
    }

    fun openServices(itemPreset: MenuServiceItem?) {
        jobOrderServices.value.let {
            _dataState.value = DataState.OpenServices(it, itemPreset)
        }
    }

    fun openProducts(itemPreset: MenuProductItem?) {
        jobOrderProducts.value.let {
            _dataState.value = DataState.OpenProducts(it, itemPreset)
        }
    }

    fun openExtras(itemPreset: MenuExtrasItem?) {
        jobOrderExtras.value.let {
            _dataState.value = DataState.OpenExtras(it, itemPreset)
        }
    }

    fun openDelivery() {
        deliveryCharge.value.let {
            _dataState.value = DataState.OpenDelivery(it)
        }
    }

    fun openDiscount() {
        discount.value.let {
            _dataState.value = DataState.OpenDiscount(it)
        }
    }

    fun openPayment() {
        if(hasAny.value != true) {
            _dataState.value = DataState.InvalidOperation("Your Job Order is empty!")
            return
        }
        if(saved.value != true) {
            _dataState.value = DataState.InvalidOperation("The Job Order has not been saved yet!")
        }
        val paymentId = _paymentId.value
        val customerId = _customerId.value
        if(paymentId != null) {
            _dataState.value = DataState.OpenPayment(paymentId, customerId)
        } else {
            _dataState.value = DataState.MakePayment(currentCustomer.value!!.id)
        }
    }

    fun openPictures() {
        _jobOrderId.value?.let {
            _dataState.value = DataState.OpenPictures(it)
        }
    }

    fun validate() {
        if(currentCustomer.value == null) {
            _dataState.value = DataState.InvalidOperation("Select customer first!", "customer")
            return
        }

        if(discountApplicable.value == false) {
            _dataState.value = DataState.InvalidOperation("Discount not applicable")
            return
        }

        if(hasAny.value != true) {
            _dataState.value = DataState.InvalidOperation("Your Job Order is empty!")
            return
        }

        if(createdAt.value?.isAfter(Instant.now()) == true) {
            _dataState.value = DataState.InvalidOperation("Invalid date and time!")
            return
        }

        viewModelScope.launch {
            val products = jobOrderProducts.value?.filter {
                !it.deleted
            }

            if(!products.isNullOrEmpty()) {
                products.let {
                    val unavailable = productsRepository.checkAll(it)
                    if(unavailable != null) {
                        _dataState.value = DataState.InvalidOperation(unavailable)
                        return@launch
                    } else {
                        _dataState.value = DataState.ProceedToSaveJO
                    }
                }
            } else {
                _dataState.value = DataState.ProceedToSaveJO
            }
        }
    }

    fun requestCancel() {
        _dataState.value = DataState.RequestCancel(_jobOrderId.value)
    }

    fun requestExit() {
        val hasAny = hasAny.value ?: false
        val saved = saved.value ?: false
        val modified = _modified.value ?: false
        val jobOrderId = _jobOrderId.value

        // modified and not saved = canceled
        // not modified = canceled
        // modified but saved = ok

        val resultCode = if(modified && saved) { RESULT_OK } else { RESULT_CANCELED }
        _dataState.value = DataState.RequestExit(
            (!saved && !hasAny) || saved,
            resultCode,
            jobOrderId
        )
    }

    /** endregion */

    fun save(userId: UUID) {
        viewModelScope.launch {
            val jobOrderNumber = jobOrderNumber.value
            val customerId = currentCustomer.value?.id ?: return@launch

            val subtotal = subtotal.value ?: 0f
            val discountInPeso = discountInPeso.value ?: 0f
            val discountedAmount = subtotal - discountInPeso
            val createdAt = createdAt.value!!
            val jobOrderId = _jobOrderId.value ?: UUID.randomUUID()
            val paymentId = _paymentId.value //payment.value?.payment?.id

            val jobOrderServices = jobOrderServices.value
            val jobOrderProducts = jobOrderProducts.value
            val jobOrderExtras = jobOrderExtras.value
            val deliveryCharge = deliveryCharge.value
            val jobOrderDiscount = discount.value

            val jobOrder = EntityJobOrder(
                jobOrderNumber,
                customerId,
                userId,
                subtotal,
                discountInPeso,
                discountedAmount,
                paymentId
            ).apply {
                this.id = jobOrderId
                this.createdAt = createdAt
                this.sync = false
            }

            val packages = jobOrderPackages.value?.map {
                EntityJobOrderPackage(
                    jobOrder.id,
                    it.packageRefId,
                    it.packageName,
                    it.totalPrice,
                    it.quantity,
                    it.isVoid,
                    it.joPackageItemId ?: UUID.randomUUID()
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            }

            val packageServices = _packageServices.map {
                it.createdAt = createdAt
                it
            }
            val packageExtras = _packageExtras.map {
                it.createdAt = createdAt
                it
            }
            val packageProducts = _packageProducts.map {
                it.createdAt = createdAt
                it
            }
//            val packageProducts = _packageProducts
//            val packageExtras = _packageExtras

            val services = jobOrderServices?.map {
                EntityJobOrderService(
                    jobOrder.id,
                    it.serviceRefId,
                    null,
                    it.name,
                    it.price,
                    it.discountedPrice,
                    it.quantity,
                    it.used,
                    it.isVoid,
                    EntityServiceRef(
                        it.serviceType,
                        it.machineType,
                        it.washType,
                        it.minutes
                    ),
                    it.joServiceItemId ?: UUID.randomUUID()
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            } ?: emptyList()

            val products = jobOrderProducts?.map {
                EntityJobOrderProduct(
                    jobOrder.id,
                    it.productRefId,
                    null,
                    it.name,
                    it.price,
                    it.discountedPrice,
                    it.measureUnit,
                    it.unitPerServe,
                    it.quantity,
                    it.productType,
                    it.isVoid,
                    it.joProductItemId ?: UUID.randomUUID()
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            } ?: emptyList()
            val extras = jobOrderExtras?.map {
                EntityJobOrderExtras(
                    jobOrder.id,
                    it.extrasRefId,
                    null,
                    it.name,
                    it.price,
                    it.discountedPrice,
                    it.quantity,
                    it.category,
                    it.isVoid,
                    it.joExtrasItemId ?: UUID.randomUUID()
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            } ?: emptyList()

            val delivery = deliveryCharge?.let {
                EntityJobOrderDeliveryCharge(
                    it.deliveryProfileId,
                    it.vehicle,
                    it.deliveryOption,
                    it.price,
                    it.discountedPrice,
                    it.distance,
                    it.isVoid,
                    jobOrder.id
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            }
            val discount = jobOrderDiscount?.let {
                EntityJobOrderDiscount(
                    it.discountRefId,
                    it.name,
                    it.value,
                    it.applicableTo,
                    it.isVoid,
                    jobOrder.id
                ).apply {
                    deleted = it.deleted
                    this.createdAt = createdAt
                }
            }
            val jobOrderWithItem = EntityJobOrderWithItems(
                jobOrder,
                packages,
                services.plus(packageServices),
                extras.plus(packageExtras),
                products.plus(packageProducts),
                delivery,
                discount
            )

            jobOrderRepository.save(jobOrderWithItem).let {
                _dataState.value = DataState.SaveSuccess(jobOrder.id, customerId)
                _saved.value = true

                loadByJobOrderId(jobOrderId)
            }
        }
    }
    fun pickCustomer() {
        val customerId = currentCustomer.value?.id
        val cannotSwitchCustomer = jobOrderServices.value?.any {
            it.used > 0
        }

        if(cannotSwitchCustomer == true) {
            _dataState.value = DataState.InvalidOperation("Cannot change customer for a Job Order with used services")
        } else {
            _dataState.value = DataState.SearchCustomer(customerId)
        }
    }
    fun openPrinterOptions() {
        _jobOrderId.value?.let {
            _dataState.value = DataState.OpenPrinter(it)
        }
    }
}