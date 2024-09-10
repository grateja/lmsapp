package com.vag.lmsapp.app.menu.main_menu

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.EndingActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.customers.list.CustomersActivity
import com.vag.lmsapp.app.reports.daily_report.DailyReportActivity
import com.vag.lmsapp.app.expenses.ExpensesActivity
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.app.menu.MenuItem
import com.vag.lmsapp.app.menu.sub_menu.SubMenuActivity
import com.vag.lmsapp.app.payment_list.PaymentListActivity
import com.vag.lmsapp.app.remote.RemoteActivationPanelActivity
import com.vag.lmsapp.databinding.ActivityMainBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.services.BacklogSyncService
import com.vag.lmsapp.util.ActivityContractsLauncher
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.calculateSpanCount
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : EndingActivity(), InternetConnectionCallback {
    companion object {
        const val LAUNCH_CODE_AUTH_STARTUP = "Open startup"
    }
    private val mainViewModel: MainMenuViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code ->
            if(code == LAUNCH_CODE_AUTH_STARTUP) {
                setupMainMenu()
                mainViewModel.setCurrentUserId(loginCredentials.userId)
            }
        }

        onCancel = {code ->
            if(code == LAUNCH_CODE_AUTH_STARTUP) {
                finish()
            }
        }
    }

    private val mainMenuAdapter = Adapter<MenuItem>(R.layout.recycler_item_main_menu).apply {
        setData(
            listOf(
                MenuItem(
                    "Dashboard",
                    "Generate and view sales reports.",
                    DailyReportActivity::class.java,
                    R.drawable.icon_sales_report,
                    permissions = listOf(
                        EnumActionPermission.VIEW_DASHBOARD
                    ),
//                backgroundColor = resources.getColor(R.color.color_code_dashboard, null)
                ),
                MenuItem(
                    "Job Orders",
                    "Manage and track job orders.",
                    JobOrderListActivity::class.java,
                    R.drawable.icon_job_orders,
//                backgroundColor = resources.getColor(R.color.color_code_job_order, null)
                ),
                MenuItem(
                    "Machines",
                    "Access and control remote machines.",
                    RemoteActivationPanelActivity::class.java,
                    R.drawable.icon_machines,
//                backgroundColor = resources.getColor(R.color.color_code_machines, null)
                ),
                MenuItem(
                    "Payments",
                    "View Payments",
                    PaymentListActivity::class.java,
//                backgroundColor = resources.getColor(R.color.color_code_payments, null)
                ),
                MenuItem(
                    "Customers",
                    "Manage customer information and profiles.",
                    CustomersActivity::class.java,
                    R.drawable.icon_customers,
//                backgroundColor = resources.getColor(R.color.color_code_customers, null)
                ),
                MenuItem(
                    "Expenses",
                    "Track and record business expenses.",
                    ExpensesActivity::class.java,
                    R.drawable.icon_expenses,
//                backgroundColor = resources.getColor(R.color.color_code_expenses, null)
                ),
//                MenuItem(
//                    "Shop Preferences",
//                    "Customize services, products, extras, pickup & delivery, packages, and discounts.",
//                    SubMenuActivity::class.java,
//                    R.drawable.icon_shop_preferences,
//                    null,
//                ),
            )
        )
    }

    private val permissionRequestLauncher = ActivityContractsLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.checkCurrentUser()

        InternetConnectionObserver
            .instance(this)
            .setCallback(this)
            .register()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        subscribeEvents()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequestLauncher.launch(arrayOf(POST_NOTIFICATIONS))
        }
    }

    private fun subscribeEvents() {
        mainViewModel.dataState.observe(this, Observer {
            when(it) {
                is MainMenuViewModel.NavigationState.OpenInitialLogin -> {
                    authViewModel.authenticate(listOf(), LAUNCH_CODE_AUTH_STARTUP, true)
                    mainViewModel.clearState()
                }
                is MainMenuViewModel.NavigationState.LoadMainMenu -> {
                    setupMainMenu()
                    mainViewModel.clearState()
                }
                else -> {}
            }
        })
        authViewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.Submit -> {
                    when(val authResult = it.data) {
                        is AuthResult.Authenticated -> {
                            if(authResult.action == LAUNCH_CODE_AUTH_STARTUP) {
                                setupMainMenu()
                            }
                        }

                        is AuthResult.MandateAuthentication -> {
                            authLauncher.launch(authResult.permissions, authResult.action, true)
                        }

                        is AuthResult.OperationNotPermitted -> {
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })
        mainMenuAdapter.onItemClick = {
            val intent = Intent(this, it.activityClass)
            startActivity(intent)
        }

        binding.cardButtonSettings?.setOnClickListener {
            val intent = Intent(this, SubMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMainMenu() {
        binding.recyclerMenu.adapter = mainMenuAdapter
        binding.recyclerMenu.layoutManager = GridLayoutManager(
            this, this.calculateSpanCount(R.dimen.menu_tile_width)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        InternetConnectionObserver.unRegister()
    }

    override fun onConnected() {
        BacklogSyncService.start(this)
        ShopSetupSyncWorker.enqueue(this, true)
    }

    override fun onDisconnected() { }
}