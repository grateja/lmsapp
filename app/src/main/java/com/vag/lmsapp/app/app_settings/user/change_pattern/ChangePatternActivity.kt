package com.vag.lmsapp.app.app_settings.user.change_pattern

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.itsxtt.patternlock.PatternLockView
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthDialogViewModel
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityChangePatternBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showMessageDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePatternActivity : AppCompatActivity() {
    companion object {
        const val ACTION_CHANGE_PATTERN = "Login with current pattern/password"
    }
    private val viewModel: ChangePatternViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityChangePatternBinding

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_CHANGE_PATTERN -> {
                viewModel.confirm(loginCredentials)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pattern)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(Constants.USER_ID).toUUID()?.let {
            viewModel.setUserId(it)
        }
    }

    private fun subscribeEvents() {
        binding.patternLock.setOnPatternListener(object : PatternLockView.OnPatternListener {
            override fun onComplete(ids: ArrayList<Int>): Boolean {
                viewModel.setInitialPattern(ids)
                return true
            }
        })
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
                    authViewModel.authenticate(listOf(), ACTION_CHANGE_PATTERN, true)
//                    authLauncherActivity.launch(listOf(EnumActionPermission.MODIFY_USERS), "Change pattern", false)
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