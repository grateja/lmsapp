package com.vag.lmsapp.app.pickup_and_deliveries.add_edit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityDeliveryProfileAddEditBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.selectAllOnFocus
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddEditDeliveryProfileActivity : CrudActivity() {
    private val viewModel: AddEditDeliveryProfileViewModel by viewModels()
    private lateinit var binding: ActivityDeliveryProfileAddEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_profile_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.textBaseFare.selectAllOnFocus()
        binding.textPricePerKm.selectAllOnFocus()

        subscribeListeners()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, androidx.lifecycle.Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    confirmExit(it.data.id)
                }
                is DataState.DeleteSuccess -> {
                    confirmExit(it.data.id)
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun get(id: UUID?) {
        viewModel.get(id)
    }

    override fun confirmDelete(loginCredentials: LoginCredentials?) {
        viewModel.confirmDelete(loginCredentials?.userId)
    }

    override fun confirmSave(loginCredentials: LoginCredentials?) {
        viewModel.save()
    }

    override fun requestExit() {
        viewModel.requestExit()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        viewModel.requestExit()
    }

    override fun confirmExit(entityId: UUID?) {
        super.confirmExit(entityId)
        viewModel.resetState()
    }
}