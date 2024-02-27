package com.vag.lmsapp.app.shop_preferences

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
    private val viewModel: ShopPreferencesViewModel by viewModels()
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
                is SettingsNavigationState.OpenStringSettings -> {
                    println(it.title)
                    println("title")
                    showTextInputDialog(it.title, it.message, it.value) { result ->
                        viewModel.update(result, it.key)
                    }
                    viewModel.resetState()
                }
//                is ShopPreferencesViewModel.DataState.ShowEditShopName -> {
//                    this.showTextInputDialog("Edit Shop name", "", it.text) { input ->
//                        viewModel.updateShopName(input)
//                    }
//                    viewModel.resetState()
//                }
//                is ShopPreferencesViewModel.DataState.ShowEditAddress -> {
//                    this.showTextInputDialog("Edit Address", "", it.text) { input ->
//                        viewModel.updateAddress(input)
//                    }
//                    viewModel.resetState()
//                }
//                is ShopPreferencesViewModel.DataState.ShowEditContactNumber -> {
//                    this.showTextInputDialog("Edit Contact number", "", it.text) { input ->
//                        viewModel.updateContactNumber(input)
//                    }
//                    viewModel.resetState()
//                }
//                is ShopPreferencesViewModel.DataState.ShowEditEmail -> {
//                    this.showTextInputDialog("Edit email", "", it.text) { input ->
//                        viewModel.updateEmail(input)
//                    }
//                    viewModel.resetState()
//                }
                else -> {}
            }
        })
    }
}