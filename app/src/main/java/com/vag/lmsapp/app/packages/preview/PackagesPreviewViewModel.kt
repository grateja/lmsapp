package com.vag.lmsapp.app.packages.preview

import androidx.lifecycle.*
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.app.packages.EnumPackageItemType
import com.vag.lmsapp.app.packages.list.PackageItem
import com.vag.lmsapp.room.entities.EntityPackage
import com.vag.lmsapp.room.entities.EntityPackageExtras
import com.vag.lmsapp.room.entities.EntityPackageProduct
import com.vag.lmsapp.room.entities.EntityPackageService
import com.vag.lmsapp.room.entities.EntityPackageWithItems
import com.vag.lmsapp.room.entities.EntityServiceRef
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PackagesPreviewViewModel

@Inject
constructor(
    private val packageRepository: JobOrderPackageRepository,
    private val servicesRepository: WashServiceRepository,
    private val productsRepository: ProductRepository,
    private val extrasRepository: ExtrasRepository
) : ViewModel() {
    private val _requireRefresh = MutableLiveData(false)

    private val _modified = MutableLiveData(false)
    val modified: LiveData<Boolean> = _modified

    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _packageId = MutableLiveData<UUID>()
    val packagePromo = _packageId.switchMap { packageRepository.getAsLiveData(it) }

    private val _availableServices = MutableLiveData<List<MenuServiceItem>?>()
    val availableServices: LiveData<List<MenuServiceItem>?> = _availableServices

    private val _availableProducts = MutableLiveData<List<MenuProductItem>?>()
    val availableProducts: LiveData<List<MenuProductItem>?> = _availableProducts

    private val _availableExtras = MutableLiveData<List<MenuExtrasItem>?>()
    val availableExtras: LiveData<List<MenuExtrasItem>?> = _availableExtras

    private val _selectedPackageItem = MutableLiveData<PackageItem>()
    val selectedPackageItem: LiveData<PackageItem> = _selectedPackageItem

    val totalPrice = MediatorLiveData<Float>().apply {
        fun update() {
            val servicesTotal = _availableServices.value?.filter {
                !it.deleted && it.selected
            }?.sumOf {
                it.price.toDouble() * it.quantity
            }?.toFloat() ?: 0f

            val productsTotal = _availableProducts.value?.filter {
                !it.deleted && it.selected
            }?.sumOf {
                it.price.toDouble() * it.quantity
            }?.toFloat() ?: 0f

            val extrasTotal = _availableExtras.value?.filter {
                !it.deleted && it.selected
            }?.sumOf {
                it.price.toDouble() * it.quantity
            }?.toFloat() ?: 0f

            value = servicesTotal + productsTotal + extrasTotal
        }
        addSource(_requireRefresh) {update()}
    }

    private fun load(id: UUID?) {
        viewModelScope.launch {
            packageRepository.getById(id)?.let { _package ->
                _packageId.value = _package.prePackage.id

                _availableServices.value = servicesRepository.menuItems().map { menuServiceItem->
                    _package.services?.find {it.serviceId == menuServiceItem.serviceRefId}?.let {
                        menuServiceItem.selected = !it.deleted
                        menuServiceItem.price = it.unitPrice
                        menuServiceItem.quantity = it.quantity
                        menuServiceItem.deleted = it.deleted
                        menuServiceItem.joServiceItemId = it.id
                    }
                    menuServiceItem
                }

                _availableProducts.value = productsRepository.menuItems().map {menuProductItem->
                    _package.products?.find {it.productId == menuProductItem.productRefId}?.let {
                        menuProductItem.selected = !it.deleted
                        menuProductItem.price = it.unitPrice
                        menuProductItem.quantity = it.quantity
                        menuProductItem.deleted = it.deleted
                        menuProductItem.joProductItemId = it.id
                    }
                    menuProductItem
                }

                _availableExtras.value = extrasRepository.menuItems().map {menuExtrasItem->
                    _package.extras?.find {it.extrasId == menuExtrasItem.extrasRefId}?.let {
                        menuExtrasItem.selected = !it.deleted
                        menuExtrasItem.price = it.unitPrice
                        menuExtrasItem.quantity = it.quantity
                        menuExtrasItem.deleted = it.deleted
                        menuExtrasItem.joExtrasItemId = it.id
                    }
                    menuExtrasItem
                }

                _requireRefresh.value = true
            }
        }
    }

    fun reset() {
        load(_packageId.value)
        _modified.value = false
    }

    fun get(id: UUID?) {
        if(_packageId.value == id) return
        load(id)
    }

//    fun preselectServices(items: List<EntityPackageService>?) {
//        _availableServices.value = _availableServices.value?.map { menuServiceItem ->
//            items?.find {it.serviceId == menuServiceItem.serviceRefId}?.let {
//                menuServiceItem.selected = !it.deleted
//                menuServiceItem.price = it.unitPrice
//                menuServiceItem.quantity = it.quantity
//                menuServiceItem.deleted = it.deleted
//            }
//            menuServiceItem
//        }
//    }
//
//    fun preselectProducts(items: List<EntityPackageProduct>?) {
//        _availableProducts.value = _availableProducts.value?.map { menuProductItem ->
//            items?.find {it.productId == menuProductItem.productRefId}?.let {
//                menuProductItem.selected = !it.deleted
//                menuProductItem.price = it.unitPrice
//                menuProductItem.quantity = it.quantity
//                menuProductItem.deleted = it.deleted
//            }
//            menuProductItem
//        }
//    }
//
//    fun preselectExtras(items: List<EntityPackageExtras>?) {
//        _availableExtras.value = _availableExtras.value?.map { menuExtrasItem ->
//            items?.find {it.extrasId == menuExtrasItem.extrasRefId}?.let {
//                menuExtrasItem.selected = !it.deleted
//                menuExtrasItem.price = it.unitPrice
//                menuExtrasItem.quantity = it.quantity
//                menuExtrasItem.deleted = it.deleted
//            }
//            menuExtrasItem
//        }
//    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun selectPackageItem(packageItem: PackageItem) {
        _selectedPackageItem.value = packageItem
        _navigationState.value = NavigationState.SelectPackageItem(packageItem)
    }

//    fun updateItem(packageItem: PackageItem) {
//        if(packageItem.packageItemType == EnumPackageItemType.WASH_DRY) {
//            val items = (_packageServices.value ?: emptyList()).toMutableSet()
//            val packageService = items.find { it.serviceId == packageItem.itemId }
//            val availableService = _availableServices.value?.find { it.serviceRefId == packageItem.itemId }
//
//            if(packageService == null && availableService != null && !packageItem.deleted) {
//                items.add(
//                    EntityPackageService(
//                    packagePromo.value?.id!!,
//                    availableService.serviceRefId,
//                    availableService.name,
//                    EntityServiceRef(
//                        availableService.serviceType,
//                        availableService.machineType,
//                        availableService.washType,
//                        availableService.minutes
//                    ),
//                        packageItem.price,
//                        packageItem.quantity,
//                ))
//            } else if(packageService != null) {
//                packageService.quantity = packageItem.quantity
//                packageService.unitPrice = packageItem.price
//                packageService.deleted = packageItem.deleted
//            }
//
//            _packageServices.value = items.toList()
//        } else if(packageItem.packageItemType == EnumPackageItemType.PRODUCTS) {
//            val items = (_packageProducts.value ?: emptyList()).toMutableSet()
//            val packageProduct = items.find { it.productId == packageItem.itemId }
//            val availableProduct = _availableProducts.value?.find { it.productRefId == packageItem.itemId }
//
//            if(packageProduct == null && availableProduct != null && !packageItem.deleted) {
//                items.add(
//                    EntityPackageProduct(
//                        packagePromo.value?.id!!,
//                        availableProduct.productRefId,
//                        availableProduct.name,
//                        availableProduct.measureUnit,
//                        availableProduct.unitPerServe,
//                        packageItem.price,
//                        availableProduct.productType,
//                        packageItem.quantity,
//                    ))
//            } else if(packageProduct != null) {
//                packageProduct.quantity = packageItem.quantity
//                packageProduct.unitPrice = packageItem.price
//                packageProduct.deleted = packageItem.deleted
//            }
//
//            _packageProducts.value = items.toList()
//        } else if(packageItem.packageItemType == EnumPackageItemType.EXTRAS) {
//            val items = (_packageExtras.value ?: emptyList()).toMutableSet()
//            val packageExtra = items.find { it.extrasId == packageItem.itemId }
//            val availableExtra = _availableExtras.value?.find { it.extrasRefId == packageItem.itemId }
//
//            if(packageExtra == null && availableExtra != null && !packageItem.deleted) {
//                items.add(
//                    EntityPackageExtras(
//                        packagePromo.value?.id!!,
//                        availableExtra.extrasRefId,
//                        availableExtra.name,
//                        availableExtra.category,
//                        packageItem.quantity,
//                        packageItem.price,
//                    ))
//            } else if(packageExtra != null) {
//                packageExtra.quantity = packageItem.quantity
//                packageExtra.unitPrice = packageItem.price
//                packageExtra.deleted = packageItem.deleted
//            }
//
//            _packageExtras.value = items.toList()
//        }
//    }

    fun updateItem(packageItem: PackageItem) {
        when(packageItem.packageItemType) {
            EnumPackageItemType.WASH_DRY -> {
                _availableServices.value?.find {
                    it.serviceRefId == packageItem.itemId
                }?.apply {
                    selected = !packageItem.deleted
                    deleted = packageItem.deleted
                    quantity = packageItem.quantity
                    price = packageItem.price
                    _navigationState.value = NavigationState.UpdateAvailableServices(this)
                }
            }

            EnumPackageItemType.PRODUCTS -> {
                _availableProducts.value?.find {
                    it.productRefId == packageItem.itemId
                }?.apply {
                    selected = !packageItem.deleted
                    deleted = packageItem.deleted
                    quantity = packageItem.quantity
                    price = packageItem.price
                    _navigationState.value = NavigationState.UpdateAvailableProducts(this)
                }
            }

            EnumPackageItemType.EXTRAS -> {
                _availableExtras.value?.find {
                    it.extrasRefId == packageItem.itemId
                }?.apply {
                    selected = !packageItem.deleted
                    deleted = packageItem.deleted
                    quantity = packageItem.quantity
                    price = packageItem.price
                    _navigationState.value = NavigationState.UpdateAvailableExtras(this)
                }
            }
        }
        _modified.value = true
        _requireRefresh.value = true
    }

    fun save() {
        viewModelScope.launch {
            val totalPrice = totalPrice.value ?: 0f

            val packageServices = _availableServices.value?.filter {
                (it.selected || it.deleted)// && it.joServiceItemId != null
            }?.map {
                EntityPackageService(
                    packagePromo.value?.id!!,
                    it.serviceRefId,
                    it.name,
                    EntityServiceRef(
                        it.serviceType,
                        it.machineType,
                        it.washType,
                        it.minutes
                    ),
                    it.price,
                    it.quantity,
                    it.joServiceItemId ?: UUID.randomUUID(),
                    it.deleted
                )
            }

            val packageExtras = _availableExtras.value?.filter {
                (it.selected || it.deleted)// && it.joExtrasItemId != null
            }?.map {
                EntityPackageExtras(
                    packagePromo.value?.id!!,
                    it.extrasRefId,
                    it.name,
                    it.category,
                    it.quantity,
                    it.price,
                    it.joExtrasItemId ?: UUID.randomUUID(),
                    it.deleted
                )
            }

            val packageProducts = _availableProducts.value?.filter {
                (it.selected || it.deleted)// && it.joProductItemId != null
            }?.map {
                EntityPackageProduct(
                    packagePromo.value?.id!!,
                    it.productRefId,
                    it.name,
                    it.measureUnit,
                    it.unitPerServe,
                    it.price,
                    it.productType,
                    it.quantity,
                    it.joProductItemId ?: UUID.randomUUID(),
                    it.deleted
                )
            }

            val packagePromo = packagePromo.value?.apply {
                this.totalPrice = totalPrice
            } ?: return@launch

            val packageWithItems = EntityPackageWithItems(
                packagePromo,
                packageServices,
                packageExtras,
                packageProducts,
            )
            packageRepository.saveAll(packageWithItems)
            _modified.value = false
        }
    }

    fun hideToggle() {
        viewModelScope.launch {
            _packageId.value.let {
                packageRepository.hideToggle(it)
            }
        }
    }

    fun initiateDelete() {
        viewModelScope.launch {
            packagePromo.value?.let {
                packageRepository.delete(it)
            }
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class SelectPackageItem(val packageItem: PackageItem): NavigationState()
        data class UpdateAvailableServices(val menuServiceItem: MenuServiceItem): NavigationState()
        data class UpdateAvailableProducts(val menuProductItem: MenuProductItem): NavigationState()
        data class UpdateAvailableExtras(val menuExtrasItem: MenuExtrasItem): NavigationState()
    }
}