package com.vag.lmsapp.app.joborders.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.room.entities.EntityJobOrderPictures
import com.vag.lmsapp.room.repository.JobOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobOrderGalleryViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _jobOrderId = MutableLiveData<UUID>()

    val jobOrderPictures = _jobOrderId.switchMap { jobOrderRepository.getPicturesAsLiveData(it) }

    fun setJobOrderId(jobOrderId: UUID) {
        _jobOrderId.value = jobOrderId
    }

    fun openPreview(photoItem: PhotoItem) {
        jobOrderPictures.value?.let {
            val index = it.indexOf(photoItem)
            _navigationState.value = NavigationState.PreviewPicture(it, index)
        }
    }
    fun attachPicture(id: UUID) {
        val jobOrderId = _jobOrderId.value ?: return
        viewModelScope.launch {
            val jobOrderPictures = EntityJobOrderPictures(
                jobOrderId,
                id
            )
            jobOrderRepository.attachPicture(jobOrderPictures)
        }
    }
    fun attachPictures(ids: List<UUID>) {
        val jobOrderId = _jobOrderId.value ?: return
        viewModelScope.launch {
            val jobOrderPictures = ids.map {
                EntityJobOrderPictures(
                    jobOrderId,
                    it
                )
            }
            jobOrderRepository.attachPictures(jobOrderPictures)
        }
    }
    fun removePicture(uriId: UUID) {
        viewModelScope.launch {
            jobOrderRepository.removePicture(uriId)
        }
    }
    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class PreviewPicture(val photoItems: List<PhotoItem>, val index: Int): NavigationState()
    }
}