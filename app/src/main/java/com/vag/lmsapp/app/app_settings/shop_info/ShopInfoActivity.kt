package com.vag.lmsapp.app.app_settings.shop_info

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityShopInfoBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ShopInfoActivity : CrudActivity() {
    private lateinit var binding: ActivityShopInfoBinding
    private val viewModel: ShopInfoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_info)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.controls.viewModel = viewModel

        subscribeListeners()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }

    override fun get(id: UUID?) {
        viewModel.get()
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun confirmSave(loginCredentials: LoginCredentials?) {
        viewModel.save()
    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    override fun confirmDelete(loginCredentials: LoginCredentials?) { }
}