package com.vag.lmsapp.app.security.select_security_type

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.settings.SecuritySettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectSecurityTypeViewModel

@Inject
constructor(
    private val securitySettingsRepository: SecuritySettingsRepository
): ViewModel() {
    private val _securityType = MutableLiveData<EnumSecurityType>()
    val securityType: LiveData<EnumSecurityType> = _securityType

    init {
        viewModelScope.launch {
            _securityType.value = securitySettingsRepository.getSecurityType()
        }
    }

    fun confirm() {
        viewModelScope.launch {
            val securityType = _securityType.value
            if(securityType != null) {
                securitySettingsRepository.updateSecurityType(securityType)
            }
        }
    }

    fun setSecurityType(securityType: EnumSecurityType) {
        _securityType.value = securityType
    }
}