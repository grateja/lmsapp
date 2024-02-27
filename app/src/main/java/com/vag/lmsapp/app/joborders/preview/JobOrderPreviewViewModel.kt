package com.vag.lmsapp.app.joborders.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.repository.JobOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderPreviewViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
) : ViewModel() {
//    private lateinit var jobOrderId: UUID
//    private lateinit var customerId: UUID

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    private val _jobOrder = MutableLiveData<EntityJobOrderWithItems>()
    val jobOrder: LiveData<EntityJobOrderWithItems> = _jobOrder

    sealed class DataState {
        data class OpenPayment(val customerId: UUID) : DataState()
    }

    fun getByJobOrderId(jobOrderId: UUID?) {
        viewModelScope.launch {
            _jobOrder.value = jobOrderRepository.getJobOrderWithItems(jobOrderId)
        }
    }

//    fun setJobOrderId(jobOrderId: UUID) {
//        this.jobOrderId = jobOrderId
//    }
//
//    fun setCustomerId(customerId: UUID) {
//        this.customerId = customerId
//    }

    fun openPayment() {
//        _dataState.value = DataState.OpenPayment(customerId)
    }
}