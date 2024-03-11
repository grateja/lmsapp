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

    val jobOrderPictures = _jobOrderId.switchMap { jobOrderRepository.getPictures(it) }

    val isDeleted = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.jobOrder?.entityJobOrderVoid != null
        }
    }

    fun openPictures(currentId: UUID) {
        jobOrderPictures.value?.let { _list ->
            val index = _list.indexOfFirst { it.id == currentId }
            _navigationState.value = NavigationState.OpenPictures(_list.map {
                PhotoItem(it.id, it.createdAt)
            }, index)
        }
    }
    fun removePicture(uriId: UUID) {
        viewModelScope.launch {
            jobOrderRepository.removePicture(uriId)
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

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class InitiateEdit(val id: UUID): NavigationState()
        data class InitiatePayment(val customerId: UUID): NavigationState()
        data class PreviewPayment(val paymentId: UUID): NavigationState()
        data class OpenPrint(val id: UUID) : NavigationState()
        data class OpenDelete(val id: UUID) : NavigationState()
        data object RequestEdit: NavigationState()
        data class Invalidate(val message: String): NavigationState()
        data class OpenPictures(val ids: List<PhotoItem>, val position: Int) : NavigationState()

        data object RequireRefresh: NavigationState()
    }
}