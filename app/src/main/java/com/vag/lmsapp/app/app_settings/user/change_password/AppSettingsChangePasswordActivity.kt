package com.vag.lmsapp.app.app_settings.user.change_password

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
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
    private val viewModel: ChangePasswordViewModel by viewModels()
    private lateinit var binding: ActivityAppSettingsChangePasswordBinding
    private val authLauncherActivity = AuthLauncherActivity(this)
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
        authLauncherActivity.onOk = { loginCredentials, _ ->
            println("on ok")
            viewModel.confirm(loginCredentials)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    authLauncherActivity.launch(listOf(EnumActionPermission.MODIFY_USERS), 1)
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