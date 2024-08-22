package com.vag.lmsapp.app.remote.activate

import android.app.Activity
import androidx.lifecycle.*
import com.vag.lmsapp.model.MachineActivationQueues
import com.vag.lmsapp.model.MachineConnectionStatus
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.room.repository.JobOrderQueuesRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.room.repository.RemoteRepository
import com.vag.lmsapp.settings.DeveloperSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RemoteActivationPreviewViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository,
    private val customerRepository: CustomerRepository,
    private val jobOrderQueuesRepository: JobOrderQueuesRepository,
    private val remoteRepository: RemoteRepository,
    private val jobOrderRepository: JobOrderRepository,
    private val settingsRepository: DeveloperSettingsRepository
): ViewModel() {
    val fakeActivationOn = settingsRepository.fakeConnectionMode

    val activateButton = MediatorLiveData<String>().apply {
        fun update() {
            value = fakeActivationOn.value?.let {
                if(it) "Test activate" else "Activate"
            }
        }
        addSource(fakeActivationOn) {update()}
    }

    private val _validationMessage = MutableLiveData<String?>()
    val validationMessage: LiveData<String?> = _validationMessage

    private val _machineActivationQueue = MutableLiveData<MachineActivationQueues>()
    val machineActivationQueue: LiveData<MachineActivationQueues> = _machineActivationQueue

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

//    private val machineId = MutableLiveData<UUID>()
    val machine = _machineActivationQueue.switchMap { machineRepository.getMachineLiveData(it.machineId) } //: LiveData<EntityMachine> = _machine


    val machineUsage = machine.switchMap { machineRepository.getMachineUsageAsLiveData(it?.activationRef?.machineUsageId) }
//    private val joServiceId = MutableLiveData<UUID>()
    val jobOrderService = _machineActivationQueue.switchMap { jobOrderQueuesRepository.getAsLiveData(it.jobOrderServiceId) } //: LiveData<EntityJobOrderService> = _jobOrderService

//    private val _customer = MutableLiveData<EntityCustomer>()
//    val customer: LiveData<EntityCustomer> = _customer
    val customer = _machineActivationQueue.switchMap {customerRepository.getCustomerAsLiveData(it.customerId) }

    val jobOrder = jobOrderService.switchMap { jobOrderRepository.getAsLiveData(it?.jobOrderId) }

//    private val _machineStatus = MutableLiveData<MachineConnectionStatus>()
//    val machineStatus: LiveData<MachineConnectionStatus> = _machineStatus
//
//    private val _message = MutableLiveData<String>()
//    val message: LiveData<String> = _message

    fun setValidationMessage(message: String?) {
        _validationMessage.value = message
    }

//    fun setMachineId(id: UUID) {
//        machineId.value = id
//        _dataState.value = DataState.CheckPending(id)
//    }

//    fun setServiceId(id: UUID) {
//        joServiceId.value = id
//    }

//    fun setCustomerId(id: UUID) {
//        viewModelScope.launch {
//            _customer.value = customerRepository.get(id)
//        }
//    }

//    fun setMachineStatus(status: MachineConnectionStatus) {
//        _machineStatus.value = status
//    }
//
//    fun setMessage(message: String?) {
//        this._message.value = message
//    }

    fun setMachineActivationQueue(machineActivationQueues: MachineActivationQueues) {
        _machineActivationQueue.value = machineActivationQueues
//        if(machineActivationQueues.machineId == machineId.value) {
//            _machineActivationQueue.value = machineActivationQueues
//            joServiceId.value = machineActivationQueues.jobOrderServiceId
//
//        }
    }

    fun prepareSubmit(userId: UUID) {
        val machineId = this.machine.value?.id ?: return
        val serviceId = this.jobOrderService.value?.id
        val customerId = this.customer.value?.id

        _dataState.value = DataState.InitiateActivation(MachineActivationQueues(machineId, serviceId, customerId, userId))
    }

    fun dismiss() {
        val status = _machineActivationQueue.value?.status
        if(status == MachineConnectionStatus.CONNECTING || status == MachineConnectionStatus.SUCCESS) {
            _dataState.value = DataState.Dismiss(Activity.RESULT_OK)
        } else {
            _dataState.value = DataState.Dismiss(Activity.RESULT_CANCELED)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun fix() {
        viewModelScope.launch {
            val machine = machine.value
            val machineId = machine?.id
            val serviceId = machine?.serviceActivationId
            if(machineId != null && serviceId != null) {
                remoteRepository.revertActivation(machineId, serviceId)
                _dataState.value = DataState.FixDataInconsistencies
            }
        }
    }

    fun updateQueue(queues: MachineActivationQueues) {
        val currentQueue = _machineActivationQueue.value
        if(currentQueue?.machineId != queues.machineId) {
            return
        } else {
            _machineActivationQueue.value = queues
        }
    }

    fun initiatePrint() {
        machineUsage.value?.let {
            _dataState.value = DataState.InitiatePrint(it)
        }
    }

    sealed class DataState {
        object StateLess: DataState()
        object FixDataInconsistencies : DataState()
        class InitiateActivation(val queue: MachineActivationQueues) : DataState()
        data class InitiatePrint(val machineUsage: EntityMachineUsageDetails): DataState()
        class Dismiss(val result: Int) : DataState()
    }
}