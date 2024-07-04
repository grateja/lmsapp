package com.vag.lmsapp.app.services.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServicePreviewViewModel
@Inject
constructor(
    private val serviceRepository: WashServiceRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _serviceId = MutableLiveData<UUID>()

    val service = _serviceId.switchMap { serviceRepository.getPreviewAsLiveData(it) }

    fun setId(serviceId: UUID) {
        _serviceId.value = serviceId
    }

    fun openEdit() {
        _serviceId.value?.let {
            _navigationState.value = NavigationState.OpenEdit(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun delete() {
        viewModelScope.launch {
            service.value?.let {
                serviceRepository.delete(it.service)
            }
        }
    }

    fun hideToggle() {
        viewModelScope.launch {
            _serviceId.value?.let {
                serviceRepository.hideToggle(it)
            }
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class OpenEdit(val serviceId: UUID): NavigationState()
    }
}