package com.vag.lmsapp.app.remote.running

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
) : ViewModel()
{
    private val machineId = MutableLiveData<UUID>()
    val runningMachine = machineId.switchMap { remoteRepository.getRunningMachine(it) } //MutableLiveData<EntityRunningMachine>()

    fun get(machineId: UUID?) {
        this.machineId.value = machineId
    }
}