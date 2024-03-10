package com.vag.lmsapp.app.joborders.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderPreviewViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _jobOrderId = MutableLiveData<UUID>()

    val jobOrder = _jobOrderId.switchMap { jobOrderRepository.getJobOrderWithItemsAsLiveData(it) }

    val isLocked = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.jobOrder?.createdAt?.isToday() == false
        }
    }

    fun getByJobOrderId(jobOrderId: UUID) {
        _jobOrderId.value = jobOrderId
    }
    fun openJobOrder() {
        _jobOrderId.value?.let {
            _navigationState.value = NavigationState.InitiateEdit(it)
        }
    }

    fun openPrint() {
        _jobOrderId.value?.let {
            _navigationState.value = NavigationState.OpenPrint(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun requestEdit() {
        jobOrder.value?.let {
            if(it.jobOrder.paymentId != null) {
                _navigationState.value = NavigationState.Invalidate("Cannot edit paid Job Order")
            } else if(it.jobOrder.createdAt.isToday()) {
                _jobOrderId.value?.let {
                    _navigationState.value = NavigationState.InitiateEdit(it)
                }
            } else {
                _navigationState.value = NavigationState.RequestEdit
            }
        }
    }

    fun openPayment() {
        jobOrder.value?.jobOrder?.let {
            val paymentId = it.paymentId
            if(paymentId == null) {
                _navigationState.value = NavigationState.InitiatePayment(it.customerId)
            } else {
                _navigationState.value = NavigationState.PreviewPayment(paymentId)
            }
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class InitiateEdit(val id: UUID): NavigationState()
        data class InitiatePayment(val customerId: UUID): NavigationState()
        data class PreviewPayment(val paymentId: UUID): NavigationState()
        data class OpenPrint(val id: UUID) : NavigationState()
        data object RequestEdit: NavigationState()
        data class Invalidate(val message: String): NavigationState()
    }
}