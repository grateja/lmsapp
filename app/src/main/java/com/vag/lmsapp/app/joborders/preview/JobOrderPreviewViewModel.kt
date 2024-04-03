package com.vag.lmsapp.app.joborders.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.isToday
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
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _previewOnly = MutableLiveData<Boolean>()
    val previewOnly: LiveData<Boolean> = _previewOnly

    private val _jobOrderId = MutableLiveData<UUID>()

    val jobOrder = _jobOrderId.switchMap { jobOrderRepository.getJobOrderWithItemsAsLiveData(it) }

    val jobOrderPictures = _jobOrderId.switchMap { jobOrderRepository.getPicturesAsLiveData(it) }

    val isLocked = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.paymentWithUser != null || it?.jobOrder?.createdAt?.isToday() != true
        }
    }

    val isDeleted = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.jobOrder?.entityJobOrderVoid != null
        }
    }

    val hasService = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.services?.any { !it.isVoid && it.deletedAt == null }
        }
    }

    val hasProduct = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.products?.any { !it.isVoid && it.deletedAt == null }
        }
    }

    val hasExtras = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.extras?.any { !it.isVoid && it.deletedAt == null }
        }
    }

    val hasDelivery = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value =it?.deliveryCharge != null && it.deliveryCharge?.isVoid != true && it.deliveryCharge?.deletedAt == null
        }
    }

    val hasDiscount = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.discount != null && it.discount?.isVoid != true && it.discount?.deletedAt == null
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
    fun openDelete() {
        _jobOrderId.value?.let {
            _navigationState.value = NavigationState.OpenDelete(it)
        }
    }

    fun requireRefresh() {
        _navigationState.value = NavigationState.RequireRefresh
    }

    fun setPreviewOnly(previewOnly: Boolean) {
        _previewOnly.value = previewOnly
    }

    fun openGallery() {
        _jobOrderId.value?.let {
            _navigationState.value = NavigationState.OpenGallery(it)
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class InitiateEdit(val id: UUID): NavigationState()
        data class InitiatePayment(val customerId: UUID): NavigationState()
        data class PreviewPayment(val paymentId: UUID): NavigationState()
        data class OpenPrint(val id: UUID) : NavigationState()
        data class OpenDelete(val id: UUID) : NavigationState()
        data object RequestEdit: NavigationState()
        data class Invalidate(val message: String): NavigationState()
        data class OpenGallery(val jobOrderId: UUID): NavigationState()

        data object RequireRefresh: NavigationState()
    }
}