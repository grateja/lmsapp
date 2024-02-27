package com.vag.lmsapp.app.main_menu

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.EndingActivity
import com.vag.lmsapp.app.app_settings.developer.AppSettingsNetworkActivity
import com.vag.lmsapp.app.customers.list.CustomersActivity
import com.vag.lmsapp.app.discounts.DiscountsActivity
import com.vag.lmsapp.app.expenses.ExpensesActivity
import com.vag.lmsapp.app.extras.ExtrasActivity
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.app.packages.PackagesActivity
import com.vag.lmsapp.app.app_settings.job_orders.AppSettingsJobOrdersActivity
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.app.app_settings.user.AppSettingsUserAccountsActivity
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.pickup_and_deliveries.PickupAndDeliveriesActivity
import com.vag.lmsapp.app.products.ProductsActivity
import com.vag.lmsapp.app.remote.RemoteActivationPanelActivity
import com.vag.lmsapp.app.dashboard.DashBoardActivity
import com.vag.lmsapp.app.payment_list.PaymentListActivity
import com.vag.lmsapp.app.services.ServicesActivity
import com.vag.lmsapp.app.shop_preferences.AppSettingsShopPreferencesActivity
import com.vag.lmsapp.databinding.ActivityMainBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.ActivityContractsLauncher
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.calculateSpanCount
import com.vag.lmsapp.viewmodels.MainViewModel
//import com.csi.palabakosys.worker.RemoteWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : EndingActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val authLauncher = ActivityLauncher(this)

    private val adapter = Adapter<MenuItem>(R.layout.recycler_item_main_menu)

    private val permissionRequestLauncher = ActivityContractsLauncher(this).apply {
        onOk = {
            println("Request granted ${it.entries}")
        }
    }

    private val menuItems by lazy {
        listOf(
            MenuItem(
                "Dashboard",
                "Generate and view sales reports.",
                DashBoardActivity::class.java,
                R.drawable.icon_sales_report,
                permissions = listOf(
                    EnumActionPermission.VIEW_DASHBOARD
                ),
                backgroundColor = resources.getColor(R.color.color_code_dashboard, null)
            ),
            MenuItem(
                "Job Orders",
                "Manage and track job orders.",
                JobOrderListActivity::class.java,
                R.drawable.icon_job_orders,
                backgroundColor = resources.getColor(R.color.color_code_job_order, null)
            ),
            MenuItem(
                "Machines",
                "Access and control remote machines.",
                RemoteActivationPanelActivity::class.java,
                R.drawable.icon_machines,
                backgroundColor = resources.getColor(R.color.color_code_machines, null)
            ),
            MenuItem(
                "Payments",
                "View Payments",
                PaymentListActivity::class.java,
                backgroundColor = resources.getColor(R.color.color_code_payments, null)
            ),
            MenuItem(
                "Customers",
                "Manage customer information and profiles.",
                CustomersActivity::class.java,
                R.drawable.icon_customers,
                backgroundColor = resources.getColor(R.color.color_code_customers, null)
            ),
            MenuItem(
                "Expenses",
                "Track and record business expenses.",
                ExpensesActivity::class.java,
                R.drawable.icon_expenses,
                backgroundColor = resources.getColor(R.color.color_code_expenses, null)
            ),
            MenuItem(
                "Shop Preferences",
                "Customize services, products, extras, pickup & delivery, packages, and discounts.",
                null,
                R.drawable.icon_shop_preferences,
                null,
                menuItems = arrayListOf(
                    MenuItem(
                        "Shop information",
                        "Give some description for your shop.",
                        AppSettingsShopPreferencesActivity::class.java,
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
                        permissions = listOf(EnumActionPermission.MODIFY_INVENTORY)
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
                        permissions = listOf(EnumActionPermission.MODIFY_SERVICES)
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
                        "Job Orders",
                        "Customize job order settings.",
                        AppSettingsJobOrdersActivity::class.java,
                        permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_JOB_ORDERS)
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
                    MenuItem(
                        "Network",
                        "Technical properties and settings of the shop. Do not modify unless adviced.",
                        AppSettingsNetworkActivity::class.java,
                        permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_IPADDRESS),
                    ),
                ),
                backgroundColor = resources.getColor(R.color.color_code_shop_preferences, null)
            ),
//            MenuItem(
//                "App Settings",
//                "Configure application settings.",
//                null,
//                R.drawable.icon_app_settings,
//                null,
//                menuItems = arrayListOf(
//                    MenuItem(
//                        "User Accounts",
//                        "Manage user accounts.",
//                        AppSettingsUserAccountsActivity::class.java,
//                        permissions = listOf(EnumActionPermission.MODIFY_USERS)
//                    ),
////                    MenuItem(
////                        "Network",
////                        "Configure network and IP address settings.",
////                        SettingsIPAddressActivity::class.java,
////                        permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_IPADDRESS)
////                    ),
//                    MenuItem(
//                        "Developer",
//                        "Technical properties and settings of the shop. Do not modify unless adviced.",
//                        NetworkSettingsActivity::class.java,
//                        permissions = listOf(EnumActionPermission.MODIFY_SETTINGS_IPADDRESS),
////                        roles = listOf(Role.DEVELOPER)
//                    ),
//                ),
//                backgroundColor = resources.getColor(R.color.color_code_app_settings, null)
//            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        adapter.setData(
            menuItems
        )

        binding.recyclerMenu.adapter = adapter
        binding.recyclerMenu.layoutManager = GridLayoutManager(
            this, this.calculateSpanCount(R.dimen.menu_tile_width)
        )

        subscribeEvents()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequestLauncher.launch(arrayOf(POST_NOTIFICATIONS))
        }
    }

    private fun subscribeEvents() {
        mainViewModel.navigationState.observe(this, Observer {
            when(it) {
                is MainViewModel.NavigationState.OpenMenu -> {
                    openMenu(it.menuItem)
                    mainViewModel.resetState()
                }
                is MainViewModel.NavigationState.RequestAuthentication -> {
                    requestPermission(it.menuItem)
                    mainViewModel.resetState()
                }

                else -> {}
            }
        })
        authLauncher.onOk = {
            when(it.data?.action) {
                AuthActionDialogActivity.AUTH_ACTION -> {
                    it.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
                        println("auth passed")
                        mainViewModel.permissionGranted(it)
                    }
                }
            }
        }
        adapter.onItemClick = {
            println("menu item")
            println(it)
            mainViewModel.openMenu(it)
        }
    }

    private fun requestPermission(menuItem: MenuItem) {
        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
            action = AuthActionDialogActivity.AUTH_ACTION
            menuItem.permissions?.let {
                putParcelableArrayListExtra(AuthActionDialogActivity.PERMISSIONS_EXTRA, ArrayList(it))
            }
//            menuItem.roles?.let {
//                putParcelableArrayListExtra(AuthActionDialogActivity.ROLES_EXTRA, ArrayList(it))
//            }
        }
        authLauncher.launch(intent)
    }

    private fun openMenu(menuItem: MenuItem) {
        val intent = if(menuItem.menuItems != null)
            Intent(this, SubMenuActivity::class.java).apply {
                putParcelableArrayListExtra(SubMenuActivity.SUB_MENU_ITEMS_EXTRA, ArrayList(menuItem.menuItems))
                putExtra(SubMenuActivity.SUB_MENU_TITLE_EXTRA, menuItem.text)
            }
        else
            Intent(this, menuItem.activityClass)

        startActivity(intent)
    }

//    private val receiver = object: BroadcastReceiver() {
//        override fun onReceive(p0: Context?, p1: Intent?) {
//            println("receiver received")
//            println(p1?.action)
//            if(p1?.action == "TestService") {
//                val data = p1.getStringExtra("data")
//                println("data from broadcast receiver")
//                println(data)
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val intentFilter = IntentFilter("TestService")
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
////        registerReceiver(receiver, intentFilter)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
//    }
//
//    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//        if(it) {
//            Toast.makeText(applicationContext, "Press teh button again", Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(applicationContext, "Fuck you", Toast.LENGTH_LONG).show()
//        }
//    }
//
//
//    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
//        // network is available for use
//        override fun onAvailable(network: Network) {
//            super.onAvailable(network)
//            println("Available")
//            network.describeContents()
//        }
//
//        // Network capabilities have changed for the network
//        override fun onCapabilitiesChanged(
//            network: Network,
//            networkCapabilities: NetworkCapabilities
//        ) {
//            super.onCapabilitiesChanged(network, networkCapabilities)
//            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
//            println(unmetered)
//        }
//
//        // lost network connection
//        override fun onLost(network: Network) {
//            super.onLost(network)
//            println("Connection lost")
//        }
//    }
//
//    private fun computeWindowSizeClasses() {
//        val metrics = WindowMetricsCalculator.getOrCreate()
//            .computeCurrentWindowMetrics(this)
//
//        val widthDp = metrics.bounds.width() /
//                resources.displayMetrics.density
//
////        binding.dpWidth.text = widthDp.toString() + " ID:" + binding.dpWidth.id.toString()
//        println(widthDp)
//    }
}