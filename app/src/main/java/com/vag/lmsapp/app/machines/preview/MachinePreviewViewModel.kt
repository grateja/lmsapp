package com.vag.lmsapp.app.machines.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.machines.preview.machine_usages.MachineUsageAdvancedFilter
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.isNotEmpty
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MachinePreviewViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
): ListViewModel<EntityMachineUsageDetails, MachineUsageAdvancedFilter>() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

//    private val _machineId = MutableLiveData<UUID?>()
//    val machineId: LiveData<UUID?> = _machineId

//    private val _customerId = MutableLiveData<UUID?>()
//    val customerId: LiveData<UUID?> = _customerId

    val machine = filterParams.switchMap { machineRepository.getMachineLiveData(it.machineId) }

    val title = MediatorLiveData<String>().apply {
        fun update() {
            val machine = machine.value
            val filterParams = filterParams.value

            if(machine != null) {
                value = machine.machineName()
            } else if(filterParams != null) {
                value = "${filterParams.machineType} ${filterParams.serviceType}ers"
            }
        }
        addSource(filterParams) {update()}
        addSource(machine) {update()}
    }

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }
        viewModelScope.launch {
            if(keyword.value.isNotEmpty()) {
                delay(500)
            }

            if(reset) {
                page = 1
            }

            val advancedFilter = filterParams.value ?: MachineUsageAdvancedFilter()

            machineRepository.filter(
                keyword.value,
                page,
                advancedFilter
            ).let {
                setResult(it.result, it.resultCount, reset)
            }
        }
    }

    fun setCustomerId(customerId: UUID?) {
        setFilterParams((filterParams.value ?: MachineUsageAdvancedFilter()).apply {
            this.customerId = customerId
        })
    }

    fun setMachineId(machineId: UUID?) {
        setFilterParams((filterParams.value ?: MachineUsageAdvancedFilter()).apply {
            this.machineId = machineId
        })
    }

    fun setMachineType(machineType: EnumMachineType?) {
        setFilterParams((filterParams.value ?: MachineUsageAdvancedFilter()).apply {
            if(machineType != null) {
                this.machineId = null
            }
            this.machineType = machineType
        })
    }

    fun setServiceType(serviceType: EnumServiceType?) {
        setFilterParams((filterParams.value ?: MachineUsageAdvancedFilter()).apply {
            if(serviceType != null) {
                this.machineId = null
            }
            this.serviceType = serviceType
        })
    }

    fun openMachineSelector() {
        filterParams.value?.machineId?.let {
            _navigationState.value = NavigationState.OpenMachineSelector(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.Stateless
    }

    fun openAdvancedFilter() {
        val filter = filterParams.value ?: MachineUsageAdvancedFilter()
        setFilterState(
            FilterState.ShowAdvancedFilter(filter)
        )
    }

    fun openEdit() {
        _navigationState.value = NavigationState.OpenEdit(filterParams.value?.machineId)
    }

    fun openDelete() {
        _navigationState.value = NavigationState.OpenDelete(filterParams.value?.machineId)
    }

    fun openPing() {
        _navigationState.value = NavigationState.OpenPing(machine.value?.ipEnd ?: 0)
    }

    sealed class NavigationState {
        data object Stateless: NavigationState()
        data class OpenMachineSelector(val machineId: UUID?): NavigationState()
        data class OpenEdit(val machineId: UUID?): NavigationState()
        data class OpenDelete(val machineId: UUID?): NavigationState()
        data class OpenPing(val ipEnd: Int): NavigationState()
    }
}