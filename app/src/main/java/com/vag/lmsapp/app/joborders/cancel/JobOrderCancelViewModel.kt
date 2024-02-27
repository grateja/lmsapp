package com.vag.lmsapp.app.joborders.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityJobOrderVoid
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderCancelViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    private val _validation = MutableLiveData(InputValidation())
    val validation: LiveData<InputValidation> = _validation

    private val _jobOrder = MutableLiveData<EntityJobOrderWithItems>()
    val jobOrder: LiveData<EntityJobOrderWithItems> = _jobOrder

    val remarks = MutableLiveData("")

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun clearError(key: String) {
        _validation.value = _validation.value?.removeError(key)
    }

    fun loadJobOrder(jobOrderId: UUID?) {
        viewModelScope.launch {
            _jobOrder.value = jobOrderRepository.getJobOrderWithItems(jobOrderId)
        }
    }

    fun validate() {
        val jobOrderWithItems = _jobOrder.value
        val inputValidation = InputValidation().apply {
            addRule("remarks", remarks.value, arrayOf(Rule.Required))
        }

        if(inputValidation.isInvalid()) {
            _validation.value = inputValidation
            return
        }

        if(jobOrderWithItems == null) {
            _dataState.value = DataState.Invalidate("Invalid Job Order or deleted")
            return
        }

        _dataState.value = DataState.ValidationPassed
    }

    fun save(userId: UUID?) {
        viewModelScope.launch {
//            val validation = _validation.value ?: InputValidation()
            val jobOrderWithItems = _jobOrder.value ?: return@launch

//            validation.addRule("remarks", remarks.value, arrayOf(Rule.Required))
//
//            if(validation.isInvalid()) {
//                _validation.value = validation
//                return@launch
//            }

//            if(jobOrderWithItems == null) {
//                _dataState.value = DataState.Invalidate("Invalid Job Order or deleted")
//                return@launch
//            }

            val jobOrderVoid = EntityJobOrderVoid(userId, remarks.value)
            jobOrderRepository.cancelJobOrder(jobOrderWithItems, jobOrderVoid).let {
                _dataState.value = DataState.SaveSuccess(jobOrderWithItems.jobOrder.id)
            }
        }
    }
}