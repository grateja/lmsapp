package com.vag.lmsapp.app.machines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachinesViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
) : ViewModel() {
    private val _filter = MutableLiveData(MachineTypeFilter(
        EnumMachineType.REGULAR,
        EnumServiceType.WASH
    ))

    private val _requireSave = MutableLiveData(false)
    val requireSave: LiveData<Boolean> = _requireSave

    val filter: LiveData<MachineTypeFilter> = _filter

    val machines = filter.switchMap { machineRepository.getListAsLiveData(it) } // machineRepository.getListAsLiveData()

    private var _arrangedMachines = mutableListOf<MachineListItem>()

    fun setMachineType(machineType: EnumMachineType, serviceType: EnumServiceType) {
        _filter.value = MachineTypeFilter(
            machineType, serviceType
        )
    }

    fun setNewPositions(machines: List<MachineListItem>) {
        _arrangedMachines = machines.toMutableList()
        _requireSave.value = true
    }

    fun refresh() {
        _filter.value = filter.value
        _requireSave.value = false
    }

    fun save() {
        viewModelScope.launch {
            machineRepository.rearrange(_arrangedMachines).let {
                _requireSave.value = false
            }
        }
    }
}