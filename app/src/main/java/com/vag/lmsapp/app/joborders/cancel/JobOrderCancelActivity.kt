package com.vag.lmsapp.app.joborders.cancel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityJobOrderCancelBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.services.JobOrderSyncService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCancelActivity : AppCompatActivity(), InternetConnectionCallback {

    companion object {
//        const val JOB_ORDER_ID = "jobOrderId"
        const val ACTION_DELETE_JOB_ORDER = "Delete Job Order"
    }

    private lateinit var binding: ActivityJobOrderCancelBinding
    private val viewModel: JobOrderCancelViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_DELETE_JOB_ORDER -> {
                viewModel.save(loginCredentials.userId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_cancel)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.cardButtonClose.setOnClickListener {
            finish()
        }

//        authLauncher.onOk = { result ->
//            result.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
//                viewModel.save(it.userId)
//            }
//        }
//        authLauncher.onOk = { loginCredentials, i ->
//            viewModel.save(loginCredentials.userId)
//        }
        intent.getStringExtra(JOB_ORDER_ID).toUUID().let {
            viewModel.loadJobOrder(it)
        }
        subscribeListeners()
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
                                .setAnchorView(binding.controls)
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

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
//                    val intent = Intent(this, AuthActionDialogActivity::class.java)
                    authViewModel.authenticate(listOf(EnumActionPermission.DELETE_JOB_ORDERS), ACTION_DELETE_JOB_ORDER, false)
//                    authLauncher.launch(listOf(EnumActionPermission.DELETE_JOB_ORDERS), "Cancel Job Order", false)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        action = ACTION_DELETE_JOB_ORDER
                        putExtra(JOB_ORDER_ID, it.data.toString())
                    })
                    finish()
                    if(internetAvailable) {
                        JobOrderSyncService.start(this, it.data)
                    }
                }
                is DataState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
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