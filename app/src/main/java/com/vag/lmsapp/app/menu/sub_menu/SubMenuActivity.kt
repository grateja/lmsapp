package com.vag.lmsapp.app.menu.sub_menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.developer.AppSettingsNetworkActivity
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.app.app_settings.shop_info.ShopInfoActivity
import com.vag.lmsapp.app.app_settings.shop_preferences.AppSettingsShopPreferencesActivity
import com.vag.lmsapp.app.app_settings.text_message_templates.TextMessageTemplateListActivity
import com.vag.lmsapp.app.app_settings.user.list.AppSettingsUserAccountsActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.discounts.list.DiscountsActivity
import com.vag.lmsapp.app.extras.list.ExtrasActivity
import com.vag.lmsapp.app.machines.MachinesActivity
import com.vag.lmsapp.app.menu.MenuItem
import com.vag.lmsapp.app.menu.main_menu.MainMenuViewModel
import com.vag.lmsapp.app.packages.list.PackagesActivity
import com.vag.lmsapp.app.pickup_and_deliveries.PickupAndDeliveriesActivity
import com.vag.lmsapp.app.products.list.ProductsActivity
import com.vag.lmsapp.app.security.select_security_type.SelectSecurityTypeActivity
import com.vag.lmsapp.app.services.ServicesActivity
import com.vag.lmsapp.databinding.ActivitySubMenuBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubMenuActivity : AppCompatActivity() {
    private val subMenuAdapter = Adapter<MenuItem>(R.layout.recycler_item_sub_menu).apply {
        setData(
            listOf(
                MenuItem(
                    "Shop information",
                    "Give some description to your shop.",
                    ShopInfoActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_SHOP_DETAILS)
                ),
                MenuItem(
                    "Machines",
                    "Some descriptions.",
                    MachinesActivity::class.java
                ),
                MenuItem(
                    "Text templates",
                    "Some descriptions.",
                    TextMessageTemplateListActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_SHOP_DETAILS)
                ),
                MenuItem(
                    "Job order settings",
                    "Modify how job orders are managed.",
                    AppSettingsShopPreferencesActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_JOB_ORDERS)
                ),
                MenuItem(
                    "Wash & Dry Services",
                    "Manage wash & dry service offerings.",
                    ServicesActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SERVICES)
                ),
                MenuItem(
                    "Products",
                    "Manage product offerings.",
                    ProductsActivity::class.java,
//                permissions = listOf(EnumActionPermission.MODIFY_INVENTORY)
                ),
                MenuItem(
                    "Extras",
                    "Manage additional service extras.",
                    ExtrasActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SERVICES)
                ),
                MenuItem(
                    "Pickup and Delivery",
                    "Configure pickup and delivery options.",
                    PickupAndDeliveriesActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_DELIVERIES)
                ),
                MenuItem(
                    "Package",
                    "Manage package offerings.",
                    PackagesActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SERVICES)
                ),
                MenuItem(
                    "Discount",
                    "Manage discount options.",
                    DiscountsActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_DISCOUNTS)
                ),
                MenuItem(
                    "Printer",
                    "Configure printer settings.",
                    SettingsPrinterActivity::class.java,
                ),
                MenuItem(
                    "User Accounts",
                    "Manage user accounts.",
                    AppSettingsUserAccountsActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_USERS)
                ),
//                    MenuItem(
//                        "My Account",
//                        "Setup username, password or pattern.",
//                        UserAccountPreviewActivity::class.java,
//                        permissions = listOf()
//                    ),
                MenuItem(
                    "Security",
                    "Setup username, password or pattern.",
                    SelectSecurityTypeActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_SECURITY)
                ),
                MenuItem(
                    "Network",
                    "Technical properties and settings of the shop. Do not modify unless advised.",
                    AppSettingsNetworkActivity::class.java,
                    permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_IPADDRESS),
                ),
            )
        )
    }
    private lateinit var binding: ActivitySubMenuBinding

    private val mainViewModel: SubMenuViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherActivity(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_menu)
        binding.lifecycleOwner = this
        binding.recyclerSubMenu.adapter = subMenuAdapter

        subscribeEvents()
    }

    private fun subscribeEvents() {
        authViewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.Submit -> {
                    when(val authResult = it.data) {
                        is AuthResult.Authenticated -> {
                            mainViewModel.openMenu(authResult.loginCredentials)
                        }

                        is AuthResult.MandateAuthentication -> {
                            authLauncher.launch(authResult.permissions, authResult.action, true)
                        }

                        is AuthResult.OperationNotPermitted -> {
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })
        mainViewModel.dataState.observe(this, Observer {
            when(it) {
                is SubMenuViewModel.NavigationState.OpenMenuWithPermission -> {
                    val intent = Intent(this, it.menuItem.activityClass).apply {
                        putExtra(AUTH_ID, it.loginCredentials.userId.toString())
                    }
                    startActivity(intent)
                    mainViewModel.clearState()
                }
                is SubMenuViewModel.NavigationState.SelectMenu -> {
                    if(it.menuItem.permissions.isNullOrEmpty()) {
                        startActivity(Intent(this, it.menuItem.activityClass))
                    } else {
                        authViewModel.authenticate(it.menuItem.permissions, it.menuItem.text, false)
                    }
                    mainViewModel.clearState()
                }

                else -> {}
            }
        })
        authLauncher.onOk = { loginCredentials, code ->
            mainViewModel.openMenu(loginCredentials)
        }
        subMenuAdapter.onItemClick = {
            mainViewModel.selectMenu(it)
        }
    }
}