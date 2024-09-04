package com.vag.lmsapp.app.main_menu

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.auth.AuthRepository
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.room.dao.DaoPackage
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.settings.SecuritySettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class MainViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val daoPackage: DaoPackage,
    private val authRepository: AuthRepository,
    private val securitySettingsRepository: SecuritySettingsRepository
) : ViewModel()
{
    val navigationState = MutableLiveData<NavigationState>()

    private var menuItem: MenuItem? = null

    fun openMenu(menuItem: MenuItem) {
        this.menuItem = menuItem
        viewModelScope.launch {
            securitySettingsRepository.getSecurityType().let {
                if(menuItem.permissions != null && it == EnumSecurityType.SENSITIVE_ACTIONS) {
                    navigationState.value = NavigationState.RequestAuthentication(menuItem)
                } else if(menuItem.permissions != null && it == EnumSecurityType.START_UP) {
                    val loginCredentials = securitySettingsRepository.verifyPermissions(menuItem.permissions)
                    if(loginCredentials != null) {
                        navigationState.value = NavigationState.OpenMenu(menuItem, loginCredentials)
                    } else {
                        navigationState.value = NavigationState.Invalidate("You do not have necessary permission to perform this action")
                    }
                } else {
                    navigationState.value = NavigationState.OpenMenu(menuItem, null)
                }
            }
        }
    }

    fun permissionGranted(loginCredentials: LoginCredentials) {
        menuItem?.let {
            navigationState.value = NavigationState.OpenMenu(it, loginCredentials)
        }
    }

    fun resetState() {
        navigationState.value = NavigationState.StateLess
    }

    fun checkSecurity() {
        viewModelScope.launch {
            securitySettingsRepository.getSecurityType().let {
                securitySettingsRepository.setCurrentUser(null)
                when(it) {
                    EnumSecurityType.START_UP -> {
                        navigationState.value = NavigationState.RequireOneTimeLogin
                    }
                    EnumSecurityType.SENSITIVE_ACTIONS -> {
                        navigationState.value = NavigationState.SetupUI
                    }
                    else -> { }
                }
            }
        }
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class Invalidate(val message: String): NavigationState()
        data class OpenMenu(val menuItem: MenuItem, val loginCredentials: LoginCredentials?) : NavigationState()
        data class RequestAuthentication(val menuItem: MenuItem) : NavigationState()
        data object RequireOneTimeLogin : NavigationState()
        data object SetupUI: NavigationState()
    }
}