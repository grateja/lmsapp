package com.vag.lmsapp.app.joborders.payment

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.vag.lmsapp.model.EnumPaymentMethod
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityCashless
//import com.csi.palabakosys.room.entities.EntityCashlessProvider
import com.vag.lmsapp.room.entities.EntityJobOrderPayment
import com.vag.lmsapp.room.repository.CustomerRepository
//import com.csi.palabakosys.room.repository.DataStoreRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.settings.JobOrderSettingsRepository
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.util.file
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderPaymentViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository,
    private val paymentRepository: PaymentRepository,
    private val customerRepository: CustomerRepository,
    private val dataStoreRepository: JobOrderSettingsRepository,
    application: Application
) : AndroidViewModel(application) {
    sealed class DataState {
        object StateLess : DataState()
        class OpenImagePreview(val paymentId: UUID?) : DataState()
        class PaymentSuccess(val payment: EntityJobOrderPayment, val jobOrderIds: ArrayList<String>) : DataState()
        class InvalidInput(val inputValidation: InputValidation) : DataState()
        class InvalidOperation(val message: String) : DataState()
        class RequestModifyDateTime(val dateTime: Instant) : DataState()
        data class OpenCamera(val paymentId: UUID): DataState()
        data object OpenFile: DataState()
        data class CopyFile(val paymentId: UUID, val uri: Uri): DataState()
        data object ValidationPassed: DataState()
        data object RemoveProofOfPayment: DataState()
        data class OpenPromptReplacePictureWithCamera(val paymentId: UUID): DataState()
        data object OpenPromptReplacePictureWithFile: DataState()
        data class InitiateDelete(val paymentId: UUID): DataState()
    }

    val requireOrNumber = dataStoreRepository.requireOrNumber
    val requirePictureOnCashlessPayment = dataStoreRepository.requirePictureOnCashlessPayment
    val paymentMethod = MutableLiveData(EnumPaymentMethod.CASH)
    val cashReceived = MutableLiveData("")
    val cashlessAmount = MutableLiveData("")
    val cashlessRefNumber = MutableLiveData("")
    val cashlessProvider = MutableLiveData("")
    val orNumber = MutableLiveData("")
    val cashlessProviders = paymentRepository.getCashlessProviders()
    val datePaid = MutableLiveData(Instant.now())

    private val _customerId = MutableLiveData<UUID?>()
    val customer = _customerId.switchMap { customerRepository.getCustomerAsLiveData(it) }

    private val _paymentId = MutableLiveData(UUID.randomUUID())
    val paymentId: LiveData<UUID> = _paymentId

    val payment = _paymentId.switchMap { paymentRepository.getPaymentWithJobOrdersAsLiveData(it) } //MutableLiveData<EntityJobOrderPaymentFull>()
    val jobOrders = _paymentId.switchMap { paymentRepository.getJobOrdersByPaymentId(it) }

    private val _inputValidation = MutableLiveData(InputValidation())
    val inputValidation: LiveData<InputValidation> = _inputValidation

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    private val _payableJobOrders = MutableLiveData<List<JobOrderPaymentMinimal>>()
    val payableJobOrders: LiveData<List<JobOrderPaymentMinimal>> = _payableJobOrders

    val payableAmount = MediatorLiveData<Float>().apply {
        fun update() {
            value = payableJobOrders.value?.sumOf { it.discountedAmount.toDouble() }?.toFloat() ?: 0f
        }
        addSource(payableJobOrders) {update()}
    }

    val amountToPay = MediatorLiveData<Float>().apply {
        fun update() {
            value = payableJobOrders.value?.filter{ it.selected }?.sumOf { it.discountedAmount.toDouble() }?.toFloat() ?: 0f
        }
        addSource(payableJobOrders) {update()}
    }

    val selectedJobOrdersCount = MediatorLiveData<Int>().apply {
        addSource(_payableJobOrders) {
            value = it.filter {it.selected}.size
        }
    }

    val remainingBalance = MediatorLiveData<Float>().apply {
        fun update() {
            value = payableJobOrders.value?.filter{ !it.selected }?.sumOf { it.discountedAmount.toDouble() }?.toFloat() ?: 0f
        }
        addSource(payableJobOrders) {update()}
    }

    val change = MediatorLiveData<Float>().apply {
        fun update() {
            val cash = cashReceived.value?.toFloatOrNull() ?: 0f
            val amountToPay = amountToPay.value ?: 0f
            val cashless = cashlessAmount.value?.toFloatOrNull() ?: 0f
            val change = (cash + cashless) - amountToPay
            value = change //if(change >= 0 && amountToPay > 0) { change } else { 0f }
        }
        addSource(cashReceived){update()}
        addSource(amountToPay){update()}
    }
    fun requestOpenCamera() {
        _paymentId.value?.let {id ->
            if(getApplication<Application>().file(id).exists()) {
                _dataState.value = DataState.OpenPromptReplacePictureWithCamera(id)
            } else {
                _dataState.value = DataState.OpenCamera(id)
            }
        }
    }
    fun requestOpenFile() {
        _paymentId.value?.let {id ->
            if(getApplication<Application>().file(id).exists()) {
                _dataState.value = DataState.OpenPromptReplacePictureWithFile
            } else {
                _dataState.value = DataState.OpenFile
            }
        }
    }
    fun attachUri(uri: Uri?) {
        val paymentId = _paymentId.value ?: return
        val _uri = uri ?: return
        _dataState.value = DataState.CopyFile(paymentId, _uri)
    }
    fun openImagePreview() {
        _dataState.value = DataState.OpenImagePreview(paymentId.value)
    }
    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun getPayment(paymentId: UUID?, customerId: UUID?) {
        _paymentId.value = paymentId
        _customerId.value = customerId
    }

    fun getUnpaidByCustomerId(customerId: UUID) {
        _customerId.value = customerId
        viewModelScope.launch {
            jobOrderRepository.getUnpaidByCustomerId(customerId).let { jo->
                _payableJobOrders.value = jo
            }
        }
    }

    fun clearError(key: String) {
        _inputValidation.value = _inputValidation.value?.removeError(key)
    }

    fun validate() {
        val validation = InputValidation()
        val requireOrNumber = requireOrNumber.value ?: false
        if(requireOrNumber) {
            validation.addRule("orNumber", orNumber.value, arrayOf(Rule.Required))
        }

        _payableJobOrders.value?.let { list ->
            if(!list.any {it.selected}) {
                _dataState.value = DataState.InvalidOperation("No selected Job Order")
                return
            }
        }

        validation.addRule("datePaid", datePaid.value, arrayOf(
            Rule.Required,
            Rule.NotAfter(Instant.now())
        ))

        if(paymentMethod.value == EnumPaymentMethod.CASH) {
            validation.addRule(
                "cashReceived",
                cashReceived.value,
                arrayOf(
                    Rule.Required,
                    Rule.IsNumeric,
                    Rule.Min(amountToPay.value, "The payment amount is insufficient.")
                )
            )
        } else if(paymentMethod.value == EnumPaymentMethod.CASHLESS) {
            validation.addRule(
                "cashlessProvider",
                cashlessProvider.value,
                arrayOf(
                    Rule.Required
                )
            )
            validation.addRule(
                "cashlessAmount",
                cashlessAmount.value,
                arrayOf(
                    Rule.Required,
                    Rule.IsNumeric,
                    Rule.Min(amountToPay.value, "The payment amount is insufficient."),
                    Rule.ExactAmount(amountToPay.value, "Exact amount only")
                )
            )
            validation.addRule(
                "cashlessRefNumber",
                cashlessRefNumber.value,
                arrayOf(
                    Rule.Required
                )
            )
            if(requirePictureOnCashlessPayment.value == true) {
                _paymentId.value?.let {
                    if(!getApplication<Application>().file(it).exists()) {
                        validation.addError("proofOfPayment", "Proof of payment is required!")
                    }
                }
            }
        } else if(paymentMethod.value == EnumPaymentMethod.MIXED) {
            val cash = cashReceived.value?.toFloatOrNull() ?: 0f
            val cashless = cashlessAmount.value?.toFloatOrNull() ?: 0f
            validation.addRule(
                "cashReceived",
                cash + cashless,
                arrayOf(
                    Rule.Required,
                    Rule.IsNumeric,
                    Rule.Min(amountToPay.value, "The payment amount is insufficient.")
                )
            )
            validation.addRule(
                "cashlessAmount",
                cash + cashless,
                arrayOf(
                    Rule.Required,
                    Rule.IsNumeric,
                    Rule.Min(amountToPay.value, "The payment amount is insufficient."),
                    Rule.Max(amountToPay.value, "Cannot pay cashless more than the required amount")
                )
            )
            validation.addRule(
                "cashlessProvider",
                cashlessProvider.value,
                arrayOf(
                    Rule.Required
                )
            )
            validation.addRule(
                "cashlessRefNumber",
                cashlessRefNumber.value,
                arrayOf(
                    Rule.Required
                )
            )
            if(requirePictureOnCashlessPayment.value == true) {
                _paymentId.value?.let {
                    if(!getApplication<Application>().file(it).exists()) {
                        validation.addError("proofOfPayment", "Proof of payment is required!")
                    }
                }
            }
        } else {
            _dataState.value = DataState.InvalidOperation("Please select payment option!")
            return
        }


        if(validation.isInvalid()) {
            _dataState.value = DataState.InvalidInput(validation)
        } else {
            _dataState.value = DataState.ValidationPassed
        }
        _inputValidation.value = validation
    }

    fun save(userId: UUID) {
        viewModelScope.launch {
            val cashless = if(paymentMethod.value == EnumPaymentMethod.CASHLESS || paymentMethod.value == EnumPaymentMethod.MIXED) {
                EntityCashless(
                    cashlessProvider.value,
                    cashlessRefNumber.value,
                    cashlessAmount.value?.toFloatOrNull()
                )
            } else null

            val id = _paymentId.value!!

            val datePaid = datePaid.value ?: Instant.now()

            val payableJobOrders = payableJobOrders.value

            val jobOrderIds = payableJobOrders?.filter { it.selected }?.map { it.id } ?: return@launch

            val cashReceived = if(paymentMethod.value == EnumPaymentMethod.CASH || paymentMethod.value == EnumPaymentMethod.MIXED) {
                cashReceived.value?.toFloatOrNull() ?: 0f
            } else 0f

            val change = change.value ?: 0f

            val payment = EntityJobOrderPayment(
                id,
                paymentMethod.value!!,
                amountToPay.value ?: 0f,
                cashReceived,
                change,
                userId,
                orNumber.value,
                cashless
            ).apply {
                createdAt = datePaid
            }
            paymentRepository.save(payment, jobOrderIds)
            _dataState.value = DataState.PaymentSuccess(payment, ArrayList(jobOrderIds.map {it.toString()}))
        }
    }

    fun selectItem(jobOrder: JobOrderPaymentMinimal) {
        _payableJobOrders.value = _payableJobOrders.value?.apply {
            this.find {it.id == jobOrder.id}?.selected = jobOrder.selected
        }
    }

    fun setDateTime(instant: Instant) {
        val inputValidation = InputValidation().apply {
            addRule("datePaid", instant, arrayOf(
                Rule.Required,
                Rule.NotAfter(Instant.now())
            ))
        }
        if(inputValidation.isInvalid()) {
            _inputValidation.value = inputValidation
            return
        }
        datePaid.value = instant
    }

    fun requestModifyDateTime() {
        datePaid.value?.let {
            _dataState.value = DataState.RequestModifyDateTime(it)
        }
    }

    fun setPaymentMethod(method: EnumPaymentMethod) {
        paymentMethod.value = method
    }

    fun deletePicture() {
        _paymentId.value.toString().let { uri ->
            try {
                val filesDir = getApplication<Application>().filesDir
                File(filesDir, Constants.PICTURES_DIR + uri).let { _file ->
                    if(_file.exists()) {
                        _file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _dataState.value = DataState.RemoveProofOfPayment
            }
        }
    }

    fun initiateDelete() {
        _paymentId.value?.let {
            _dataState.value = DataState.InitiateDelete(it)
        }
    }
}