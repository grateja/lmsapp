package com.vag.lmsapp.app.remote

import androidx.lifecycle.LiveData
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
class RemotePanelViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
) : ViewModel() {
    private val _filter = MutableLiveData(MachineTypeFilter(
        EnumMachineType.REGULAR,
        EnumServiceType.WASH
    ))

    val filter: LiveData<MachineTypeFilter> = _filter

//    private val _machineType = MutableLiveData(EnumMachineType.REGULAR_WASHER)
//    val machineType: LiveData<EnumMachineType> = _machineType
//
//    private val _serviceType = MutableLiveData(EnumServiceType.WASH)
//    val serviceType: LiveData<EnumServiceType> = _serviceType

//    val filter = MediatorLiveData<MachineTypeFilter>().apply {
//        fun update() {
//            value = MachineTypeFilter(
//                machineType.value,
//                serviceType.value
//            )
//        }
//        addSource(machineType) {update()}
//        addSource(serviceType) {update()}
//    }

    val machines = _filter.switchMap { machineRepository.getListAsLiveData(it) } // machineRepository.getListAsLiveData()

    private val _isWiFiConnected = MutableLiveData<Boolean>()
    val isWiFiConnected: LiveData<Boolean> = _isWiFiConnected

    fun setWiFiConnectionState(isConnected: Boolean) {
        _isWiFiConnected.value = isConnected
    }

    fun setMachineType(machineType: EnumMachineType, serviceType: EnumServiceType) {
        _filter.value = MachineTypeFilter(
            machineType, serviceType
        )
    }
}