package com.vag.lmsapp.app.app_settings.job_orders

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.SettingsViewModel
import com.vag.lmsapp.settings.JobOrderSettingsRepository
import com.vag.lmsapp.util.SettingsNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsJobOrdersViewModel

@Inject
constructor(
    private val repository: JobOrderSettingsRepository
): SettingsViewModel(repository) {
    val jobOrderMaxUnpaid = repository.maxUnpaidJobOrderLimit
    val requireOrNumber = repository.requireOrNumber

    fun updateRequireOrNumber(value: Boolean) {
        viewModelScope.launch {
            repository.updateRequireOrNumber(value)
        }
    }
    fun showMaxUnpaidJobOrder() {
        jobOrderMaxUnpaid.value.let {
            navigationState.value = SettingsNavigationState.OpenIntSettings(it as Int,
                JobOrderSettingsRepository.JOB_ORDER_MAX_UNPAID,
                "Max unpaid JO",
                "Enter the maximum allowed number of unpaid Job Order per customer")
        }
    }
}