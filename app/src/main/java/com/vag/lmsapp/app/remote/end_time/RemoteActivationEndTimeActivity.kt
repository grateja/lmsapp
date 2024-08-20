package com.vag.lmsapp.app.remote.end_time

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityRemoteActivationEndTimeBinding
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationEndTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoteActivationEndTimeBinding
    private val viewModel: RemoteActivationEndTimeViewModel by viewModels()
    lateinit var authLauncherActivity: AuthLauncherActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_end_time)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        authLauncherActivity = AuthLauncherActivity(this)

        subscribeEvents()
        subscribeListener()

        intent.getStringExtra(ID).toUUID()?.let {
            viewModel.setMachineId(it)
        }
    }

    private fun subscribeEvents() {
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
        binding.cardButtonClose.setOnClickListener {
            finish()
        }
        authLauncherActivity.onOk = { loginCredentials, i ->
            if(i == 1) {
                viewModel.save(loginCredentials.userId)
            }
        }
    }

    private fun subscribeListener() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.resetState()
                    authLauncherActivity.launch(listOf(), 1)
                }

                is DataState.SaveSuccess -> {
                    viewModel.resetState()
                    finish()
                }

                is DataState.Invalidate -> {
                    viewModel.resetState()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })
    }
}