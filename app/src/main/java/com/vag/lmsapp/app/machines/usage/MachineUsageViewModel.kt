package com.vag.lmsapp.app.machines.usage

import androidx.lifecycle.*
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MachineUsageViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
): ListViewModel<EntityMachineUsageDetails, Nothing>() {
    private val _machineId = MutableLiveData<UUID?>()

    private val _dateFilter = MutableLiveData<DateFilter?>()
    val dateFilter: MutableLiveData<DateFilter?> = _dateFilter

    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    val machineType = MutableLiveData<EnumMachineType?>()

    val serviceType = MutableLiveData<EnumServiceType?>()

    private val _machineUsage = MutableLiveData<EntityMachineUsageDetails>()
    val machineUsage: LiveData<EntityMachineUsageDetails> = _machineUsage

    fun setMachineId(machineId: UUID?) {
        _machineId.value = machineId
    }

//    fun setDateFilter(dateFilter: DateFilter) {
//        _dateFilter.value = dateFilter
//    }

    fun setMachineType(machineType: EnumMachineType?) {
        this.machineType.value = machineType
    }

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }
        job = viewModelScope.launch {
            delay(500)
            if(reset) {
                page = 1
            }
            val machineId = _machineId.value
            val machineType = machineType.value
            val serviceType = serviceType.value
            val dateFilter = _dateFilter.value
            val keyword = keyword.value
            val items = machineRepository.getMachineUsage(machineId, MachineTypeFilter(
                machineType,
                serviceType
            ), keyword, page, dateFilter)
            setResult(items, null, reset)
//            _dataState.value = DataState.LoadItems(items, reset)
        }
    }
    fun clearDates() {
        _dateFilter.value = null
    }

    fun showDatePicker() {
        _dateFilter.value.let {
            val dateFilter = it ?: DateFilter(LocalDate.now(), null)
            _navigationState.value = NavigationState.OpenDateFilter(dateFilter)
        }
    }

    override fun clearState() {
        _navigationState.value = NavigationState.StateLess
        super.clearState()
    }

    fun setCurrentItem(machineUsage: EntityMachineUsageDetails) {
        _machineUsage.value = machineUsage
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateFilter(val dateFilter: DateFilter): NavigationState()
    }
}