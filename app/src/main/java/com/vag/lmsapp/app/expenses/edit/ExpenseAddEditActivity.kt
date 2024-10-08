package com.vag.lmsapp.app.expenses.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityExpenseAddEditBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.services.ExpenseSyncService
import com.vag.lmsapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExpenseAddEditActivity(
//    override var requireAuthOnSave: Boolean = true,
//    override var requireAuthOnDelete: Boolean = true
) : CrudActivity(), InternetConnectionCallback {
    companion object {
        const val MODIFY_DATE_ACTION = 1
    }

    private lateinit var binding: ActivityExpenseAddEditBinding
    private val viewModel: ExpenseAddEditViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val dateTimePicker = DateTimePicker(this)
    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_SAVE -> {
                viewModel.save(loginCredentials.userId)
            }

            ACTION_DELETE -> {
                viewModel.confirmDelete(loginCredentials.userId)
            }
        }
    }

//     private val launcher = AuthLauncherActivity(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.controls.viewModel = viewModel

        subscribeListeners()
        subscribeEvents()

        window.statusBarColor = getColor(R.color.white)
        binding.textAmount.selectAllOnFocus()
    }

    private fun subscribeEvents() {
        binding.textDatePaid.setOnClickListener {
            viewModel.showDateTimePicker()
//            launcher.launch(emptyList(), MODIFY_DATE_ACTION)
//            val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//                action = MODIFY_DATE_ACTION
//            }
//            launcher.launch(intent)
        }

//        launcher.onOk = { loginCredentials, code ->
//            if(code == MODIFY_DATE_ACTION) {
//                viewModel.showDateTimePicker()
//            }
//        }

        dateTimePicker.onDateTimeSelected = {
            viewModel.setDateCreated(it)
        }

        binding.textTag.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.post {
                    binding.textTag.selectAll()
                }
            }
        }
    }

    private fun subscribeListeners() {
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
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                        // is AuthResult.NoAuthentication -> {
                        //     setupUi()
                        //  }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    authViewModel.authenticate(listOf(), ACTION_SAVE, false)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    if(internetAvailable) {
                        ExpenseSyncService.start(this, it.data.id)
                    }
                    confirmExit(it.data.id)
                    viewModel.resetState()
                }
                is DataState.DeleteSuccess -> {
                    if(internetAvailable) {
                        ExpenseSyncService.start(this, it.data.id)
                    }
                    confirmExit(it.data.id)
                    viewModel.resetState()
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is ExpenseAddEditViewModel.NavigationState.ShowDateTimePicker -> {
                    dateTimePicker.show(it.dateTime)
                    viewModel.resetNavigationState()
                }
                else -> {}
            }
        })

        viewModel.tags.observe(this, Observer {
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, it)
            binding.textTag.setAdapter(adapter)

        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.requestExit()
    }

    override fun get(id: UUID?) {
        viewModel.get(id)
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun onDelete() {
        showDeleteConfirmationDialog {
            authViewModel.authenticate(listOf(), ACTION_DELETE, false)
        }
    }
//
//    override fun confirmDelete(loginCredentials: LoginCredentials?) {
//        viewModel.confirmDelete(loginCredentials?.userId)
//    }

//    override fun confirmSave(loginCredentials: LoginCredentials?) {
//        viewModel.save(loginCredentials?.userId)
//    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }

    var internetAvailable = false

    override fun onPause() {
        super.onPause()
        InternetConnectionObserver.unRegister()
    }

    override fun onConnected() {
        internetAvailable = true
        println("internet available")
    }

    override fun onDisconnected() {
        internetAvailable = false
        println("no internet available")
    }
    override fun onResume() {
        super.onResume()

        InternetConnectionObserver
            .instance(this)
            .setCallback(this)
            .register()
    }
}