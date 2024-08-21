package com.vag.lmsapp.app.app_settings.user.change_pattern

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.itsxtt.patternlock.PatternLockView
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthDialogViewModel
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
    private val viewModel: ChangePatternViewModel by viewModels()
    private lateinit var binding: ActivityChangePatternBinding
    private val authLauncherActivity = AuthLauncherActivity(this)

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
        authLauncherActivity.onOk = { loginCredentials, _ ->
            println("on ok")
            viewModel.confirm(loginCredentials)
        }
        binding.patternLock.setOnPatternListener(object : PatternLockView.OnPatternListener {
            override fun onComplete(ids: ArrayList<Int>): Boolean {
                viewModel.setInitialPattern(ids)
                return true
            }
        })
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