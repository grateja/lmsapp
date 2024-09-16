package com.vag.lmsapp.app.menu.sub_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
//import com.vag.lmsapp.app.daily_report.DailyReportActivity
import com.vag.lmsapp.app.menu.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel

class SettingsViewModel
@Inject
constructor(
) : ViewModel()
{
    private val _dataState = MutableLiveData<NavigationState>()
    val dataState: LiveData<NavigationState> = _dataState

    private var _menuItem: MenuItem? = null

    fun selectMenu(menuItem: MenuItem) {
        _menuItem = menuItem
        _dataState.value = NavigationState.SelectMenu(menuItem)
    }

    fun openMenu(loginCredentials: LoginCredentials) {
        _menuItem?.let {
            _dataState.value = NavigationState.OpenMenuWithPermission(
                it, loginCredentials
            )
        }
    }

    fun clearState() {
        _dataState.value = NavigationState.StateLess
    }


    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class SelectMenu(val menuItem: MenuItem): NavigationState()
        data class OpenMenuWithPermission(
            val menuItem: MenuItem,
            val loginCredentials: LoginCredentials
        ): NavigationState()
    }
}