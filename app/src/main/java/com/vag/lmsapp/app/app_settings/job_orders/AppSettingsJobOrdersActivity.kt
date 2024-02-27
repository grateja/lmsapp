package com.vag.lmsapp.app.app_settings.job_orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityAppSettingsJobOrdersBinding
import com.vag.lmsapp.util.SettingsNavigationState
import com.vag.lmsapp.util.showTextInputDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsJobOrdersActivity : AppCompatActivity() {
    private val viewModel: AppSettingsJobOrdersViewModel by viewModels()
    private lateinit var binding: ActivityAppSettingsJobOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_job_orders)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        binding.cardMaxUnpaidJo.card.setOnClickListener {
            viewModel.showMaxUnpaidJobOrder()
        }
        binding.switchRequireOrNumber.setOnCheckedChangeListener { _, checked ->
            viewModel.updateRequireOrNumber(checked)
        }
    }

    private fun subscribeListeners() {
        viewModel.settingsNavigationState.observe(this, Observer {
            when(it) {
                is SettingsNavigationState.OpenIntSettings -> {
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