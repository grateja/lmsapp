package com.vag.lmsapp.app.joborders.remarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.entities.EntityJobOrder
import com.vag.lmsapp.room.entities.EntityJobOrderVoid
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobOrderRemarksViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    private val _validation = MutableLiveData(InputValidation())
    val validation: LiveData<InputValidation> = _validation

    private val _jobOrderId = MutableLiveData<UUID>()

    private val _jobOrder = MutableLiveData<EntityJobOrder>()
    val jobOrder: LiveData<EntityJobOrder> = _jobOrder

    val remarks = MutableLiveData("")

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun loadJobOrder(jobOrderId: UUID) {
        if(_jobOrderId.value != jobOrderId) {
            _jobOrderId.value = jobOrderId
            viewModelScope.launch {
                jobOrderRepository.get(jobOrderId)?.let {
                    remarks.value = it.remarks
                    _jobOrder.value = it
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            _jobOrderId.value?.let {
                jobOrderRepository.updateRemarks(it, remarks.value)
                _dataState.value = DataState.SaveSuccess(it)
            }
        }
    }
}