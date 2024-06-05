package com.vag.lmsapp.app.remote.running

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.room.repository.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RemoteRunningViewModel

@Inject
constructor(
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val machineId = MutableLiveData<UUID>()
    val runningMachine = machineId.switchMap { remoteRepository.getRunningMachine(it) }

    fun get(machineId: UUID?) {
        this.machineId.value = machineId
    }

    fun initiateEntTime() {
        runningMachine.value?.let {
            _navigationState.value = NavigationState.InitiateEndTime(it.machine.id)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class InitiateEndTime(val machineId: UUID): NavigationState()
    }
}