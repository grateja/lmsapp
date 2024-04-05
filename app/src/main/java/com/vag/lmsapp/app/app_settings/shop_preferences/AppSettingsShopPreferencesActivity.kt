package com.vag.lmsapp.app.app_settings.shop_preferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityAppSettingsShopPreferencesBinding
import com.vag.lmsapp.util.SettingsNavigationState
import com.vag.lmsapp.util.showTextInputDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsShopPreferencesActivity : AppCompatActivity() {
    private val viewModel: AppSettingsShopPreferencesViewModel by viewModels()
    private lateinit var binding: ActivityAppSettingsShopPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_shop_preferences)
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
        binding.switchRequirePictureOnCashlessPayment.setOnCheckedChangeListener { _, checked ->
            viewModel.updateRequirePictureOnCashlessPayment(checked)
        }
        binding.cardShopName.card.setOnClickListener {
            viewModel.showEditShopName()
        }
        binding.cardAddress.card.setOnClickListener {
            viewModel.showEditAddress()
        }
        binding.cardContactNumber.card.setOnClickListener {
            viewModel.showEditContactNumber()
        }
        binding.cardEmail.card.setOnClickListener {
            viewModel.showEditEmail()
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
                is SettingsNavigationState.OpenStringSettings -> {
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