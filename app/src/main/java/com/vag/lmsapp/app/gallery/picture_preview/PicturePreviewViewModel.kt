package com.vag.lmsapp.app.gallery.picture_preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.JobOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PicturePreviewViewModel

@Inject
constructor(
    val jobOrderRepository: JobOrderRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    private val _uriId = MutableLiveData<List<PhotoItem>>()
    val uriIds: LiveData<List<PhotoItem>> = _uriId

    fun setUriIds(id: List<PhotoItem>) {
        _uriId.value = id
    }

    fun deletePicture(photoItem: PhotoItem) {
        viewModelScope.launch {
            jobOrderRepository.removePicture(photoItem.id)
            _dataState.value = DataState.RemovePicture(photoItem)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    sealed class DataState {
        object StateLess: DataState()
        data class RemovePicture(val photoItem: PhotoItem): DataState()
    }
}