package com.vag.lmsapp.app.extras.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.ExtrasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExtrasPreviewViewModel

@Inject
constructor(
    private val repository: ExtrasRepository
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _extrasId = MutableLiveData<UUID>()

    val extras = _extrasId.switchMap { repository.getAsLiveData(it) }

    fun get(extrasId: UUID) {
        _extrasId.value = extrasId
    }

    fun openEdit() {
        _extrasId.value?.let {
            _navigationState.value = NavigationState.OpenEdit(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun delete() {
        viewModelScope.launch {
            extras.value?.let {
                repository.delete(it.extras)
                _navigationState.value = NavigationState.DeleteSuccess(it.extras.id)
            }
        }
    }

    fun hideToggle() {
        viewModelScope.launch {
            _extrasId.value?.let {
                repository.hideToggle(it)
            }
        }
    }


    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class OpenEdit(val serviceId: UUID): NavigationState()
        data class DeleteSuccess(val serviceId: UUID): NavigationState()
    }
}