package com.vag.lmsapp.app.joborders.payment.delete

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityJobOrderPaymentDeleteBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.file
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPaymentDeleteActivity : AppCompatActivity() {
    private val viewModel: JobOrderPaymentDeleteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityJobOrderPaymentDeleteBinding

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_DELETE_PAYMENT -> {
                viewModel.confirm(loginCredentials.userId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_payment_delete)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        intent.getStringExtra(PAYMENT_ID).toUUID()?.let {
            viewModel.setPaymentId(it)
        }

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
//        authLauncher.onOk = { loginCredentials, code ->
//            viewModel.confirm(loginCredentials.userId)
//        }
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.cardButtonClose.setOnClickListener {
            finish()
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
                    authViewModel.authenticate(listOf(EnumActionPermission.MODIFY_JOB_ORDER_PAYMENTS), ACTION_DELETE_PAYMENT, false)
//                    authLauncher.launch(listOf(EnumActionPermission.MODIFY_JOB_ORDER_PAYMENTS), "Delete payment", false)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    file(it.data).let {
                        if(it.exists()) {
                            it.delete()
                        }
                    }
                    setResult(RESULT_OK)
                    finish()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    companion object {
        const val ACTION_DELETE_PAYMENT = "Delete payment"
    }
}