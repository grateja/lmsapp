package com.vag.lmsapp.app.app_settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.settings.BaseSettingsRepository
import com.vag.lmsapp.util.SettingsNavigationState
import kotlinx.coroutines.launch

open class SettingsViewModel(private val _repository: BaseSettingsRepository): ViewModel() {
    protected val navigationState = MutableLiveData<SettingsNavigationState>()
    val settingsNavigationState: LiveData<SettingsNavigationState> = navigationState

    fun resetState() {
        navigationState.value = SettingsNavigationState.StateLess
    }

    fun <T>update(value: T, key: String) {
        viewModelScope.launch {
            _repository.update(value, key)
        }
    }
}