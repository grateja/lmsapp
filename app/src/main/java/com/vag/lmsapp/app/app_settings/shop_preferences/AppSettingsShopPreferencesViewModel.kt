package com.vag.lmsapp.app.app_settings.shop_preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.SettingsViewModel
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.settings.JobOrderSettingsRepository
import com.vag.lmsapp.util.SettingsNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsShopPreferencesViewModel

@Inject
constructor(
    private val settingsRepository: JobOrderSettingsRepository,
): SettingsViewModel(settingsRepository) {
    val jobOrderMaxUnpaid = settingsRepository.maxUnpaidJobOrderLimit
    val requireOrNumber = settingsRepository.requireOrNumber
    val requirePictureOnCashlessPayment = settingsRepository.requirePictureOnCashlessPayment

//    val shopName = settingsRepository.shopName
//    val address = settingsRepository.address
//    val contactNumber = settingsRepository.contactNumber
//    val email = settingsRepository.email

    fun updateRequirePictureOnCashlessPayment(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateRequirePictureOnCashlessPayment(value)
        }
    }
    fun updateRequireOrNumber(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateRequireOrNumber(value)
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
//    fun showEditShopName() {
//        (shopName.value ?: "").let {
//            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.SHOP_NAME,"Edit shop name", "It will be printed in the Job Order and Claim stub!")
//        }
//    }

//    fun showEditAddress() {
//        (address.value ?: "").let {
//            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.ADDRESS,"Edit shop address", "It will be printed in the Job Order and Claim stub!")
//        }
//    }

//    fun showEditContactNumber() {
//        (contactNumber.value ?: "").let {
//            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.CONTACT_NUMBER,"Edit shop contact number", "It will be printed in the Job Order and Claim stub!")
//        }
//    }

//    fun showEditEmail() {
//        (email.value ?: "").let {
//            navigationState.value = SettingsNavigationState.OpenStringSettings(it, JobOrderSettingsRepository.EMAIL,"Edit shop email", "It will be printed in the Job Order and Claim stub!")
//        }
//    }
}