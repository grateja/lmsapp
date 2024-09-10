package com.vag.lmsapp.app.auth

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.settings.SecuritySettingsRepository
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel

@Inject
constructor(
    private val authUseCase: AuthUseCase,
//    private val settingsRepository: SecuritySettingsRepository
): ViewModel() {
//    val currentUser = settingsRepository.currentUser
//
//    private val _securityTypeOrdinal = settingsRepository.securityTypeOrdinal
//    val securityType = MediatorLiveData<EnumSecurityType>().apply {
//        addSource(_securityTypeOrdinal) {
//            value = EnumSecurityType.entries[it]
//        }
//    }

    private val _dataState = MutableLiveData<DataState<AuthResult>>()
    val dataState: LiveData<DataState<AuthResult>> = _dataState

    fun authenticate(permissions: List<EnumActionPermission>, action: String, mandate: Boolean, intent: Intent? = null) {
        viewModelScope.launch {
            _dataState.value = DataState.Submit(authUseCase(permissions, action, mandate, intent))
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}