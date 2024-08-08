package com.vag.lmsapp.app.machines.machine_selector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MachineSelectorViewModel

@Inject
constructor(
    private val repository: MachineRepository
) : ViewModel() {
    private val _filter = MutableLiveData(
            MachineTypeFilter(
            EnumMachineType.REGULAR,
            EnumServiceType.WASH
        )
    )

    private val _machineId = MutableLiveData<UUID?>()
    val machineId: LiveData<UUID?> = _machineId

    val filter: LiveData<MachineTypeFilter> = _filter

    val machines = _filter.switchMap { repository.getListAsLiveData(it) } // machineRepository.getListAsLiveData()

    fun setMachineType(machineType: EnumMachineType, serviceType: EnumServiceType) {
        _filter.value = MachineTypeFilter(
            machineType, serviceType
        )
    }

    fun setActiveMachine(machineId: UUID?) {
        _machineId.value = machineId
    }
}