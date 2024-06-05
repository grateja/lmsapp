package com.vag.lmsapp.app.remote.end_time

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.room.repository.RemoteRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RemoteActivationEndTimeViewModel

@Inject
constructor(
    private val remoteRepository: RemoteRepository,
    private val machineRepository: MachineRepository
) : ViewModel() {
    val remarks = MutableLiveData("")

    private val _machineId = MutableLiveData<UUID>()
    val machine = _machineId.switchMap { machineRepository.getMachineLiveData(it) }

    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    private val _inputValidation = MutableLiveData(InputValidation())
    val inputValidation: LiveData<InputValidation> = _inputValidation

    fun setMachineId(id: UUID) {
        _machineId.value = id
    }

    fun clearError(key: String) {
        _inputValidation.value = _inputValidation.value?.apply {
            this.removeError(key)
        }
    }

    fun save(userId: UUID) {
        viewModelScope.launch {
            val machine = machine.value
            if(machine == null) {
                _dataState.value = DataState.Invalidate("Invalid machine ID")
                return@launch
            }
            val remarks = remarks.value ?: return@launch
            remoteRepository.endTime(remarks, machine, userId)
            _dataState.value = DataState.SaveSuccess(machine.id)
        }
    }

    fun validate() {
        val validation = InputValidation().apply {
            addRule("remarks", remarks.value, arrayOf(Rule.Required))
        }

        _inputValidation.value = validation

        if(validation.isInvalid()) {
            _dataState.value = DataState.InvalidInput(validation)
        } else {
            _dataState.value = DataState.ValidationPassed
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}