package com.vag.lmsapp.app.app_settings.user.change_pattern

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import java.util.ArrayList
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChangePatternViewModel

@Inject
constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState<UUID>>()
    val dataState: LiveData<DataState<UUID>> = _dataState

    val _userId = MutableLiveData<UUID>()

    private val _inputValidation = MutableLiveData(InputValidation())
    val inputValidation: LiveData<InputValidation> = _inputValidation

    private val _initialPattern = MutableLiveData<ArrayList<Int>?>()

    val title = MediatorLiveData<String>().apply {
        addSource(_initialPattern) {
            value = if(it == null) {
                "Set new pattern"
            } else {
                "Confirm new pattern"
            }
        }
    }

    val user = _userId.switchMap { userRepository.getByIdAsLiveData(it) }

    fun setUserId(userId: UUID) {
        _userId.value = userId
    }

    fun clearError(key: String = "") {
        _inputValidation.value = _inputValidation.value?.removeError(key)
    }

    fun confirm(loginCredentials: LoginCredentials) {
        println("confirm")
        val userId = _userId.value
        if(user.value?.user?.role == Role.OWNER && userId != loginCredentials.userId) {
            _dataState.value = DataState.Invalidate("You are not the owner of this account!")
            return
        } else if(loginCredentials.role == Role.STAFF) {
            _dataState.value = DataState.Invalidate("Only owners can edit staff's password")
            return
        }
        viewModelScope.launch {
            userRepository.changePattern(userId!!, _initialPattern.value!!)
            _dataState.value = DataState.SaveSuccess(userId)
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun setInitialPattern(ids: ArrayList<Int>) {
        val initialPattern = _initialPattern.value

        if(initialPattern == null) {
            _initialPattern.value = ids
        } else if(initialPattern == ids) {
            _dataState.value = DataState.ValidationPassed
        } else {
            _dataState.value = DataState.Invalidate("Confirm pattern is incorrect")
        }
    }
}