package com.vag.lmsapp.app.machines.options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.room.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MachineOptionsViewModel

@Inject
constructor(
    private val repository: MachineRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _machineId = MutableLiveData<UUID>()

    val machine = _machineId.switchMap { repository.getMachineLiveData(it) }

    fun setMachineId(machineId: UUID) {
        _machineId.value = machineId
    }

    fun openMachineHistory() {
        _machineId.value?.let {
            _navigationState.value = NavigationState.OpenMachineHistory(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun openMachineConfiguration() {
        _machineId.value?.let {
            _navigationState.value = NavigationState.OpenMachineConfiguration(it)
        }
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenMachineHistory(val machineId: UUID) : NavigationState()
        data class OpenMachineConfiguration(val machineId: UUID) : NavigationState()
    }
}