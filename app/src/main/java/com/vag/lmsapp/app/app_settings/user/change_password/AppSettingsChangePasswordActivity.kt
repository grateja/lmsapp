package com.vag.lmsapp.app.app_settings.user.change_password

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
import com.vag.lmsapp.databinding.ActivityAppSettingsChangePasswordBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showMessageDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsChangePasswordActivity : AppCompatActivity() {
    companion object {
        const val ACTION_CHANGE_PASSWORD = "Login with current pattern/password"
    }
    private val viewModel: ChangePasswordViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_CHANGE_PASSWORD -> {
                viewModel.confirm(loginCredentials)
            }
        }
    }

    private lateinit var binding: ActivityAppSettingsChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_change_password)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(USER_ID).toUUID()?.let {
            viewModel.setUserId(it)
        }
    }

    private fun subscribeEvents() {
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
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
                    authViewModel.authenticate(listOf(), ACTION_CHANGE_PASSWORD, true)
//                    authLauncher.launch(listOf(EnumActionPermission.MODIFY_USERS), "Change password", false)
                    viewModel.resetState()
                }

                is DataState.SaveSuccess -> {
                    viewModel.resetState()
                    finish()
                }

                is DataState.Invalidate -> {
                    showMessageDialog("Invalid operation", it.message)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }
}