package com.vag.lmsapp.app.joborders.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.MoshiHelper
import com.vag.lmsapp.util.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderPreviewViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository,
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
            value = it?.paymentWithUser != null || it?.jobOrder?.createdAt?.isToday() != true || it.jobOrder.entityJobOrderVoid != null
        }
    }

    val isDeleted = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.jobOrder?.deleted == true
        }
    }

    val isVoid = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.jobOrder?.entityJobOrderVoid != null
        }
    }

    val allowEdit = MediatorLiveData<Boolean>().apply {
        fun update() {
            val jobOrder = jobOrder.value
            value = jobOrder?.jobOrder?.entityJobOrderVoid == null
                    && jobOrder?.paymentWithUser == null
                    && jobOrder?.jobOrder?.deleted == false
                    && _previewOnly.value != true
        }
        addSource(jobOrder) {update()}
        addSource(_previewOnly) {update()}
    }

    val allowDelete = MediatorLiveData<Boolean>().apply {
        fun update() {
            val jobOrder = jobOrder.value
            value = jobOrder?.jobOrder?.entityJobOrderVoid == null
                    && jobOrder?.paymentWithUser == null
                    && jobOrder?.jobOrder?.deleted == false
        }
        addSource(jobOrder) {update()}
    }

    val hasPackages = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.packages?.any { !it.deleted }
        }
    }

    val hasService = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.services?.any { !it.deleted && it.jobOrderPackageId == null }
        }
    }

    val hasProduct = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.products?.any { !it.deleted && it.jobOrderPackageId == null }
        }
    }

    val hasExtras = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.extras?.any { !it.deleted && it.jobOrderPackageId == null }
        }
    }

    val hasDelivery = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.deliveryCharge != null && it.deliveryCharge?.deleted == false
        }
    }

    val hasDiscount = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.discount != null && it.discount?.deleted == false
        }
    }

    val hasPayment = MediatorLiveData<Boolean>().apply {
        addSource(jobOrder) {
            value = it?.paymentWithUser != null && it.paymentWithUser?.payment?.deleted == false
        }
    }

//    fun test(jobOrder: EntityJobOrderWithItems) {
//        viewModelScope.launch {
//            networkRepository.sendJobOrder(jobOrder, UUID.fromString("ea570cb2-0448-4924-b58c-d48912de638b")).let {
//                println("req result")
//                println(it)
//            }
//        }
//    }

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

//    fun makePayment() {
//        jobOrder.value?.customer?.id?.let {
//            _navigationState.value = NavigationState.MakePayment(it)
//        }
//    }
//    fun editPayment() {
//        jobOrder.value?.jobOrder?.paymentId?.let {
//            _navigationState.value = NavigationState.EditPayment(it)
//        }
//    }
    fun openPayment() {
        if(hasPayment.value == true) {
            jobOrder.value?.jobOrder?.let {
                _navigationState.value = NavigationState.OpenPayment(it.paymentId!!, it.customerId)
            }
        } else {
            jobOrder.value?.customer?.id?.let {
                _navigationState.value = NavigationState.MakePayment(it)
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
        data class MakePayment(val customerId: UUID): NavigationState()
        data class OpenPayment(val paymentId: UUID, val customerId: UUID): NavigationState()
        data class OpenPrint(val id: UUID) : NavigationState()
        data class OpenDelete(val id: UUID) : NavigationState()
        data object RequestEdit: NavigationState()
        data class Invalidate(val message: String): NavigationState()
        data class OpenGallery(val jobOrderId: UUID): NavigationState()

        data object RequireRefresh: NavigationState()
    }
}