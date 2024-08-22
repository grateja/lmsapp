package com.vag.lmsapp.app.remote.running

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.printer.PrinterPreviewActivity
import com.vag.lmsapp.app.remote.end_time.RemoteActivationEndTimeActivity
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.room.repository.RemoteRepository
import com.vag.lmsapp.util.Constants.Companion.MACHINE_ID
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RemoteRunningViewModel

@Inject
constructor(
    private val remoteRepository: RemoteRepository,
    private val machineRepository: MachineRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    private val machineId = MutableLiveData<UUID>()
    val runningMachine = machineId.switchMap { remoteRepository.getRunningMachine(it) }

    val machineUsage = runningMachine.switchMap { machineRepository.getMachineUsageAsLiveData(it?.machineUsage?.id) }

    fun get(machineId: UUID?) {
        this.machineId.value = machineId
    }

    fun initiateEntTime() {
        runningMachine.value?.let {
            val bundle = Bundle().apply {
                putString(MACHINE_ID, it.machine.id.toString())
            }
            _dataState.value = DataState.OpenActivity(RemoteActivationEndTimeActivity::class.java, bundle)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun initiatePrint() {
        machineUsage.value?.let {
            val bundle = Bundle().apply {
                putParcelableArrayList(PrinterPreviewActivity.PRINTER_ITEMS_EXTRA, ArrayList(it.printItems()))
            }
            _dataState.value = DataState.OpenActivity(PrinterPreviewActivity::class.java, bundle)
        }
    }
}