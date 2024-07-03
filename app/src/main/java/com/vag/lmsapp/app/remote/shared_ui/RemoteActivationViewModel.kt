package com.vag.lmsapp.app.remote.shared_ui

import androidx.lifecycle.*
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.room.repository.JobOrderQueuesRepository
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RemoteActivationViewModel

@Inject
constructor(
    private val queuesRepository: JobOrderQueuesRepository,
    private val machineRepository: MachineRepository
) : ViewModel() {
    val machineId = MutableLiveData<UUID?>()
    val machine = machineId.switchMap { machineRepository.getMachineLiveData(it) }
    val customerQueues = machine.switchMap { queuesRepository.getByMachineType(
        MachineTypeFilter(it?.machineType, it?.serviceType)
    ) }

    val dataState = MutableLiveData<DataState>()
//    val machine = MutableLiveData<EntityMachine>()
    val service = MutableLiveData<EntityAvailableService>()

//    val customerQueues = MutableLiveData<List<EntityCustomerQueueService>>()
    val customerQueue = MutableLiveData<EntityCustomerQueueService>()

    val availableServices = MutableLiveData<List<EntityAvailableService>>()

    fun resetState() {
        dataState.value = DataState.StateLess
    }

    fun selectMachine(machineId: UUID?) {
        this.machineId.value = machineId
//        this.machine.value = machine
//        this.loadCustomerQueuesByMachineType(machine?.machineType)
        this.dataState.value = DataState.SelectMachine(machineId)
    }

//    private fun loadCustomerQueuesByMachineType(machineType: EnumMachineType?) {
//        viewModelScope.launch {
//            machineType?.let {
//                queuesRepository.getByMachineType(it).let { customers ->
//                    customerQueues.value = customers
//                }
//            }
//        }
//    }

    fun selectCustomer(customerQueue: EntityCustomerQueueService) {
        this.customerQueue.value = customerQueue
        this.getQueuesByCustomer(customerQueue)
        this.dataState.value = DataState.SelectCustomer
    }

    private fun getQueuesByCustomer(queueService: EntityCustomerQueueService) {
        viewModelScope.launch {
            queuesRepository.getAvailableServiceByCustomerId(queueService.customerId,
                MachineTypeFilter(
                    queueService.machineType,
                    queueService.serviceType
                )
            ).let {
                availableServices.value = it
            }
        }
    }

    fun selectService(service: EntityAvailableService) {
        this.service.value = service
        this.dataState.value = DataState.SelectService
        this.activate()
    }


    private fun activate() {
        val machineId = this.machine.value?.id ?: return
        val serviceId = this.service.value?.id ?: return
        val customerId = this.customerQueue.value?.customerId ?: return

        dataState.value = DataState.PrepareActivation(machineId, serviceId, customerId)
    }

    sealed class DataState {
        object StateLess: DataState()
        class SelectMachine(val machineId: UUID?): DataState()
        object SelectCustomer: DataState()
        object SelectService: DataState()
        class PrepareActivation(val machineId: UUID, val serviceId: UUID, val customerId: UUID) : DataState()
//        class InitiateConnection(val queue: MachineActivationQueues) : DataState()
    }
}