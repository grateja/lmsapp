package com.vag.lmsapp.app.app_settings.user.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountPreviewViewModel

@Inject
constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _userId = MutableLiveData<UUID>()
    val user = _userId.switchMap { repository.getByIdAsLiveData(it) }

    private val _authId = MutableLiveData<UUID>()

    fun setAuth(authId: UUID) {
        _authId.value = authId
    }

    val canEdit = MediatorLiveData<Boolean>().apply {
        fun update() {
            val user = user.value ?: return
            val authId = _authId.value ?: return
            value = if(user.user.role == Role.OWNER) {
                // current user is the authorized user
                user.user.id == authId
            } else {
                true
            }
        }
        addSource(_authId) {update()}
        addSource(user) {update()}
    }

    fun setUserId(userId: UUID) {
        _userId.value = userId
    }
}