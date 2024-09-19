package com.vag.lmsapp.app.menu.main_menu

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.EndingActivity
import com.vag.lmsapp.app.app_settings.user.preview.UserAccountPreviewActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.customers.list.CustomersActivity
import com.vag.lmsapp.app.reports.summary_report.SummaryReportActivity
import com.vag.lmsapp.app.expenses.ExpensesActivity
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.app.menu.MenuItem
import com.vag.lmsapp.app.menu.sub_menu.SettingsActivity
import com.vag.lmsapp.app.payment_list.PaymentListActivity
import com.vag.lmsapp.app.remote.RemoteActivationPanelActivity
import com.vag.lmsapp.app.reports.yearly_report.YearlyReportActivity
import com.vag.lmsapp.databinding.ActivityMainBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.services.BacklogSyncService
import com.vag.lmsapp.util.ActivityContractsLauncher
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.AUTH_ID
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.calculateSpanCount
import com.vag.lmsapp.util.periodicAsyncTask
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class MainActivity : EndingActivity(), InternetConnectionCallback {
    companion object {
        const val LAUNCH_CODE_AUTH_STARTUP = "Open startup"
        const val LAUNCH_CODE_AUTH_OPEN_REPORT = "Open report"
        const val LAUNCH_CODE_AUTH_OPEN_ACCOUNT = "Open account"
    }
    private val mainViewModel: MainMenuViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code) }

        onCancel = {code ->
            if(code == LAUNCH_CODE_AUTH_STARTUP) {
                finish()
            }
        }
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            LAUNCH_CODE_AUTH_STARTUP -> {
                setupMainMenu()
                mainViewModel.setCurrentUserId(loginCredentials.userId)
            }

            LAUNCH_CODE_AUTH_OPEN_REPORT -> openReport(loginCredentials.permissions)

            LAUNCH_CODE_AUTH_OPEN_ACCOUNT -> openAccount(loginCredentials.userId)
        }
    }

    private val mainMenuAdapter = Adapter<MenuItem>(R.layout.recycler_item_main_menu).apply {
        setData(
            listOf(
//                MenuItem(
//                    "Reports",
//                    "Generate and view sales reports.",
//                    YearlyReportActivity::class.java,
//                    R.drawable.icon_sales_report,
//                    permissions = listOf()
//                ),
//                MenuItem(
//                    "Dashboard",
//                    "Generate and view sales reports.",
//                    SummaryReportActivity::class.java,
//                    R.drawable.icon_sales_report,
//                    permissions = listOf(
//                        EnumActionPermission.VIEW_DASHBOARD
//                    ),
////                backgroundColor = resources.getColor(R.color.color_code_dashboard, null)
//                ),
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
        lifecycleScope.launch {
            11.periodicAsyncTask(5) { stepped, iteration ->
                println("iteration $iteration $stepped")
                delay(500)
            }
        }


        super.onCreate(savedInstanceState)

        mainViewModel.checkCurrentUser()

        InternetConnectionObserver
            .instance(this)
            .setCallback(this)
            .register()

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
                            permitted(authResult.loginCredentials, authResult.action)
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
    }

    private fun openReport(permissions: List<EnumActionPermission>) {
        val permitted = EnumActionPermission.deniedPermissions(
            permissions, listOf(EnumActionPermission.VIEW_DAILY_REPORTS)
        ).isEmpty()

        val intent = if(permitted) {
            Intent(this, YearlyReportActivity::class.java)
        } else {
            Intent(this, SummaryReportActivity::class.java)
        }
        startActivity(intent)
    }

    private fun openAccount(userId: UUID) {
        val intent = Intent(this, UserAccountPreviewActivity::class.java).apply {
            putExtra(AUTH_ID, userId.toString())
            putExtra(USER_ID, userId.toString())
        }
        startActivity(intent)
    }

    private fun setupMainMenu() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerMenu.adapter = mainMenuAdapter
        binding.recyclerMenu.layoutManager = GridLayoutManager(
            this, this.calculateSpanCount(R.dimen.menu_tile_width)
        )
        binding.cardButtonSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.cardButtonReport?.setOnClickListener {
            authViewModel.authenticate(listOf(), LAUNCH_CODE_AUTH_OPEN_REPORT, false)
        }
        binding.cardButtonAccount?.setOnClickListener {
            authViewModel.authenticate(listOf(), LAUNCH_CODE_AUTH_OPEN_ACCOUNT, false)
        }
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