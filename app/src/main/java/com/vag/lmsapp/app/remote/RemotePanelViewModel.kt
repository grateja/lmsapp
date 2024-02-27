package com.vag.lmsapp.app.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemotePanelViewModel

@Inject
constructor(
    private val machineRepository: MachineRepository
) : ViewModel() {
    private val machineType = MutableLiveData(EnumMachineType.REGULAR_WASHER)
    val machines = machineType.switchMap { machineRepository.getListAsLiveData(it) } // machineRepository.getListAsLiveData()

    private val _isWiFiConnected = MutableLiveData<Boolean>()
    val isWiFiConnected: LiveData<Boolean> = _isWiFiConnected

    fun setWiFiConnectionState(isConnected: Boolean) {
        _isWiFiConnected.value = isConnected
    }

    fun setMachineType(machineType: String) {
        this.machineType.value = EnumMachineType.fromName(machineType)
    }
}