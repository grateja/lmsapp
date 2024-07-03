package com.vag.lmsapp.app.machines

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MachinesViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
) : ViewModel() {
    private val machineType = MutableLiveData(EnumMachineType.REGULAR)
    private val serviceType = MutableLiveData(EnumServiceType.WASH)
    private val filter = MediatorLiveData<MachineTypeFilter>().apply {
        fun update() {
            value = MachineTypeFilter(
                machineType.value!!,
                serviceType.value!!
            )
        }
        addSource(machineType) {update()}
        addSource(serviceType) {update()}
    }
    val machines = filter.switchMap { machineRepository.getListAsLiveData(it) } // machineRepository.getListAsLiveData()

    fun setMachineType(machineType: String) {
        this.machineType.value = EnumMachineType.fromName(machineType)
    }
}