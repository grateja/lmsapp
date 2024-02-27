package com.vag.lmsapp.app.app_settings.developer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityAppSettingsNetworkBinding
import com.vag.lmsapp.settings.DeveloperSettingsRepository
import com.vag.lmsapp.util.SettingsNavigationState
import com.vag.lmsapp.util.showTextInputDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsNetworkActivity : AppCompatActivity() {
    private val viewModel: DeveloperSettingsViewModel by viewModels()
    private lateinit var binding: ActivityAppSettingsNetworkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_network)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        subscribeEvents()
        subscribeListeners()
    }
    private fun subscribeEvents() {
        binding.cardActivationDelay.card.setOnClickListener {
            viewModel.openConnectionDelayDialog()
        }
        binding.checkboxFakeConnectionModeOn.setOnCheckedChangeListener { _, checked ->
            viewModel.update(checked, DeveloperSettingsRepository.DEVELOPER_FAKE_CONNECTION_MODE_ON)
        }
        binding.cardPrefix.card.setOnClickListener {
            viewModel.showEditPrefix()
        }
        binding.cardSubnet.card.setOnClickListener {
            viewModel.showEditSubnetMask()
        }
        binding.cardEndpoint.card.setOnClickListener {
            viewModel.showEditEndpoint()
        }
        binding.cardTimeout.card.setOnClickListener {
            viewModel.showEditTimeout()
        }
    }
    private fun subscribeListeners() {
        viewModel.settingsNavigationState.observe(this, Observer {
            when(it) {
                is SettingsNavigationState.OpenStringSettings -> {
                    showTextInputDialog(it.title, it.message, it.value) { result ->
                        viewModel.update(result, it.key)
                    }
                    viewModel.resetState()
                }
                is SettingsNavigationState.OpenLongSettings -> {
                    showTextInputDialog(it.title, it.message, it.value) { result ->
                        viewModel.update(result, it.key)
                    }
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}