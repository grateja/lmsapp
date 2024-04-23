package com.vag.lmsapp.app.app_settings.user.change_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel

@Inject
constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    val _userId = MutableLiveData<UUID>()
    val newPassword = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val _inputValidation = MutableLiveData(InputValidation())
    val inputValidation: LiveData<InputValidation> = _inputValidation

    val user = _userId.switchMap { userRepository.getByIdAsLiveData(it) }

    fun setUserId(userId: UUID) {
        _userId.value = userId
    }

    fun validate() {
        _inputValidation.value = InputValidation().apply {
            addRule("password", newPassword.value, arrayOf(Rule.Required))
            addRule("confirmPassword", confirmPassword.value, arrayOf(Rule.Required, Rule.Matched(newPassword.value, "Password do not matched!")))
        }.also {
            if(it.isInvalid()) {
                _dataState.value = DataState.InvalidInput(it)
            } else {
                _dataState.value = DataState.ValidationPassed
            }
        }
    }

    fun clearError(key: String = "") {
        _inputValidation.value = _inputValidation.value?.removeError(key)
    }

    fun confirm(loginCredentials: LoginCredentials) {
        println("confirm")
        val userId = _userId.value
        val newPassword = newPassword.value
        if(user.value?.user?.role == Role.OWNER && userId != loginCredentials.userId) {
            _dataState.value = DataState.Invalidate("You are not the owner of this account!")
            return
        } else if(loginCredentials.role == Role.STAFF) {
            _dataState.value = DataState.Invalidate("Only owners can edit staff's password")
            return
        }
        viewModelScope.launch {
            println("tangina")
            userRepository.changePassword(userId!!, newPassword!!)
            _dataState.value = DataState.SaveSuccess(userId)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}