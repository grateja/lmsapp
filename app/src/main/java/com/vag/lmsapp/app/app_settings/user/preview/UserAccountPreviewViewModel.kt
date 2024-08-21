package com.vag.lmsapp.app.app_settings.user.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountPreviewViewModel

@Inject
constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _userId = MutableLiveData<UUID>()
    val user = _userId.switchMap { repository.getByIdAsLiveData(it) }

    private val _authorizedUserId = MutableLiveData<UUID>()

    fun setAuth(authId: UUID) {
        _authorizedUserId.value = authId
    }

    val canEdit = MediatorLiveData<Boolean>().apply {
        fun update() {
            val user = user.value ?: return
            val authId = _authorizedUserId.value ?: return
            value = if(user.user.role == Role.OWNER) {
                // current user is the authorized user
                user.user.id == authId
            } else {
                true
            }
        }
        addSource(_authorizedUserId) {update()}
        addSource(user) {update()}
    }

    fun setUserId(userId: UUID) {
        _userId.value = userId
    }

    fun resetState() {
        _navigationState.value = NavigationState.Stateless
    }

    fun delete() {
        viewModelScope.launch {
            user.value?.let {
                if(it.user.role == Role.OWNER) {
                    val owners = repository.getOwners()
                    if(owners.size == 1) {
                        _navigationState.value = NavigationState.Invalidate("Cannot delete user. At least 1 admin is required")
                        return@launch
                    }
                }

                repository.delete(it.user)
                _navigationState.value = NavigationState.DeleteSuccess
            }
        }
    }

    fun openEdit() {
        val userId = _userId.value ?: return
        val authId = _authorizedUserId.value ?: return
        _navigationState.value = NavigationState.OpenEdit(userId, authId)
    }

    fun openChangePassword() {
        _userId.value?.let {
            _navigationState.value = NavigationState.ChangePassword(it)
        }
    }

    fun openChangePattern() {
        _userId.value?.let {
            _navigationState.value = NavigationState.ChangePattern(it)
        }
    }

    sealed class NavigationState {
        data object Stateless: NavigationState()
        data class OpenEdit(val userId: UUID, val authorizedUserId: UUID): NavigationState()
        data class Invalidate(val message: String): NavigationState()
        data object DeleteSuccess: NavigationState()
        data class ChangePassword(val userId: UUID): NavigationState()
        data class ChangePattern(val userId: UUID): NavigationState()
    }
}