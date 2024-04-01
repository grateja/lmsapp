package com.vag.lmsapp.app.app_settings.user.change_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.repository.UserRepository
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
    private val _userId = MutableLiveData<UUID>()
    val newPassword = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val _inputValidation = MutableLiveData(InputValidation())
    val inputValidation: LiveData<InputValidation> = _inputValidation

    val user = _userId.switchMap { userRepository.getByIdAsLiveData(it) }

    fun setUserId(userId: UUID) {
        _userId.value = userId
    }

    fun changePassword() {
        viewModelScope.launch {
            val newPassword = newPassword.value
            val confirmPassword = confirmPassword.value
            val userId = _userId.value

            val inputValidation = InputValidation().apply {
                addRule("password", newPassword, arrayOf(Rule.Required))
                addRule("confirmPassword", confirmPassword, arrayOf(Rule.Required, Rule.Matched(newPassword, "Password do not matched!")))
            }

            if(inputValidation.isInvalid()) {
                _inputValidation.value = inputValidation
            } else {
                userRepository.changePassword(userId!!, newPassword!!)
            }
        }
    }
}