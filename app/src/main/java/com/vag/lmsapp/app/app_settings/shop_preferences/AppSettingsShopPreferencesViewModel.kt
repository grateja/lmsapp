package com.vag.lmsapp.app.app_settings.shop_preferences

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.SettingsViewModel
import com.vag.lmsapp.settings.JobOrderSettingsRepository
import com.vag.lmsapp.util.SettingsNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsShopPreferencesViewModel

@Inject
constructor(
    private val repository: JobOrderSettingsRepository
): SettingsViewModel(repository) {
    val jobOrderMaxUnpaid = repository.maxUnpaidJobOrderLimit
    val requireOrNumber = repository.requireOrNumber

    val shopName = repository.shopName
    val address = repository.address
    val contactNumber = repository.contactNumber
    val email = repository.email
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
    fun showEditShopName() {
        (shopName.value ?: "").let {
            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.SHOP_NAME,"Edit shop name", "")
        }
    }

    fun showEditAddress() {
        (address.value ?: "").let {
            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.ADDRESS,"Edit shop address", "")
        }
    }

    fun showEditContactNumber() {
        (contactNumber.value ?: "").let {
            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.CONTACT_NUMBER,"Edit shop contact number", "")
        }
    }

    fun showEditEmail() {
        (email.value ?: "").let {
            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.EMAIL,"Edit shop email", "")
        }
    }
}