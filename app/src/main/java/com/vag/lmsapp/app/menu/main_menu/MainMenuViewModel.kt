package com.vag.lmsapp.app.menu.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.vag.lmsapp.app.daily_report.DailyReportActivity
import com.vag.lmsapp.app.menu.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel

class MainMenuViewModel
@Inject
constructor(
) : ViewModel()
{
    private val _currentUserId = MutableLiveData<UUID?>()

    fun setCurrentUserId(userId: UUID) {
        _currentUserId.value = userId
    }

    fun checkCurrentUser() {
        _currentUserId.value.let {
            if(it == null) {
                _dataState.value = NavigationState.OpenInitialLogin
            } else {
                _dataState.value = NavigationState.LoadMainMenu
            }
        }
    }

    private val _dataState = MutableLiveData<NavigationState>()
    val dataState: LiveData<NavigationState> = _dataState

    private var _menuItem: MenuItem? = null

    fun clearState() {
        _dataState.value = NavigationState.StateLess
    }


    sealed class NavigationState {
        data object StateLess: NavigationState()
        data object OpenInitialLogin: NavigationState()
        data class SelectMenu(val menuItem: MenuItem): NavigationState()
        data object LoadMainMenu: NavigationState()
    }
}