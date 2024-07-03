package com.vag.lmsapp.app.services.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityServicesAddEditBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddEditServiceActivity(
//    override var requireAuthOnSave: Boolean = false,
//    override var requireAuthOnDelete: Boolean = false
) : CrudActivity() {
    companion object {
        const val SERVICE_ID_EXTRA = "serviceId"
        const val MACHINE_TYPE_EXTRA = "machineType"
        const val SERVICE_TYPE_EXTRA = "serviceType"
    }
    private lateinit var binding: ActivityServicesAddEditBinding
    private val viewModel: AddEditServiceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_services_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.controls.viewModel = viewModel

        subscribeListeners()
        subscribeEvents()

    }

    override fun get(id: UUID?) {
        val serviceId = intent.getStringExtra(SERVICE_ID_EXTRA)?.toUUID()
        val machineType = intent.getParcelableExtra(MACHINE_TYPE_EXTRA) ?: EnumMachineType.REGULAR
        val serviceType = intent.getParcelableExtra(SERVICE_TYPE_EXTRA) ?: EnumServiceType.WASH
        viewModel.get(serviceId, machineType, serviceType)
    }

    override fun onSave() {
        viewModel.validate()
    }

    override fun confirmSave(loginCredentials: LoginCredentials?) {
        viewModel.save()
    }


    override fun confirmDelete(loginCredentials: LoginCredentials?) {
        viewModel.confirmDelete(loginCredentials?.userId)
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

//    private fun subscribeEvents() {
//        binding.controls.buttonSave.setOnClickListener {
//            viewModel.validate()
//        }
//        binding.controls.buttonCancel.setOnClickListener {
//            viewModel.requestExit()
//        }
//    }
    private fun subscribeEvents() {
        binding.cardServiceTypeWash.setOnClickListener {
            viewModel.setServiceType(EnumServiceType.WASH)
        }
        binding.cardServiceTypeDry.setOnClickListener {
            viewModel.setServiceType(EnumServiceType.DRY)
        }
        binding.cardMachineType8kg.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR)
        }
        binding.cardMachineType12kg.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                }
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(MACHINE_TYPE_EXTRA, it.data.serviceRef.machineType as Parcelable)
                    })
                    ShopSetupSyncWorker.enqueue(this)
                    viewModel.resetState()
                    finish()
                }
                is DataState.DeleteSuccess -> {
                    ShopSetupSyncWorker.enqueue(this)
                    viewModel.resetState()
                    finish()
                }
                is DataState.RequestExit -> {
                    confirmExit(it.promptPass)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}