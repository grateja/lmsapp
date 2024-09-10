package com.vag.lmsapp.app.menu.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.R
import com.vag.lmsapp.app.app_settings.developer.AppSettingsNetworkActivity
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.app.app_settings.shop_info.ShopInfoActivity
import com.vag.lmsapp.app.app_settings.shop_preferences.AppSettingsShopPreferencesActivity
import com.vag.lmsapp.app.app_settings.text_message_templates.TextMessageTemplateListActivity
import com.vag.lmsapp.app.app_settings.user.list.AppSettingsUserAccountsActivity
import com.vag.lmsapp.app.auth.AuthRepository
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.customers.list.CustomersActivity
import com.vag.lmsapp.app.daily_report.DailyReportActivity
import com.vag.lmsapp.app.discounts.list.DiscountsActivity
import com.vag.lmsapp.app.expenses.ExpensesActivity
import com.vag.lmsapp.app.extras.list.ExtrasActivity
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.app.machines.MachinesActivity
import com.vag.lmsapp.app.menu.MenuItem
import com.vag.lmsapp.app.menu.sub_menu.SubMenuActivity
import com.vag.lmsapp.app.packages.list.PackagesActivity
import com.vag.lmsapp.app.payment_list.PaymentListActivity
import com.vag.lmsapp.app.pickup_and_deliveries.PickupAndDeliveriesActivity
import com.vag.lmsapp.app.products.list.ProductsActivity
import com.vag.lmsapp.app.remote.RemoteActivationPanelActivity
import com.vag.lmsapp.app.security.select_security_type.SelectSecurityTypeActivity
import com.vag.lmsapp.app.services.ServicesActivity
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.room.dao.DaoPackage
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.settings.SecuritySettingsRepository
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