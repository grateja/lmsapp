package com.vag.lmsapp.app.security.current_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.settings.SecuritySettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CurrentUserViewModel

@Inject
constructor(
    private val securitySettingsRepository: SecuritySettingsRepository
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    val currentUser = securitySettingsRepository.currentUser

    private val _securityTypeOrdinal = securitySettingsRepository.securityTypeOrdinal
    val securityType = MediatorLiveData<EnumSecurityType>().apply {
        addSource(_securityTypeOrdinal) {
            value = EnumSecurityType.entries[it]
        }
    }

    fun switchUser() {
        viewModelScope.launch {
//            securitySettingsRepository.setCurrentUser(null)
            _navigationState.value = NavigationState.SwitchUser
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun openAccount() {
        currentUser.value?.let {
            _navigationState.value = NavigationState.OpenAccount(it.user.id)
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data object SwitchUser: NavigationState()
        data class OpenAccount(val userId: UUID): NavigationState()
    }
}