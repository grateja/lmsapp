package com.vag.lmsapp.app.services.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityServicesAddEditBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.selectAllOnFocus
import com.vag.lmsapp.util.toUUID
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddEditServiceActivity(
//    override var requireAuthOnSave: Boolean = false,
//    override var requireAuthOnDelete: Boolean = false
) : AppCompatActivity() {
    companion object {
//        const val SERVICE_ID_EXTRA = "serviceId"
        const val MACHINE_TYPE_FILTER = "filter"
    }
    private lateinit var binding: ActivityServicesAddEditBinding
    private val viewModel: AddEditServiceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_services_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeListeners()
        subscribeEvents()

        binding.textPrice.selectAllOnFocus()
        binding.textMinutes.selectAllOnFocus()

        get()
    }

    private fun get() {
        val serviceId = intent.getStringExtra(ENTITY_ID)?.toUUID()
        val filter = intent.getParcelableExtra<MachineTypeFilter>(MACHINE_TYPE_FILTER)
        viewModel.get(serviceId, filter)
    }

//    override fun onSave() {
//        viewModel.validate()
//    }

//    override fun confirmSave(loginCredentials: LoginCredentials?) {
//        viewModel.save()
//    }


//    override fun confirmDelete(loginCredentials: LoginCredentials?) {
//        viewModel.confirmDelete(loginCredentials?.userId)
//    }

//    override fun requestExit() {
//        viewModel.requestExit()
//    }

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

        binding.cardDelicate.setOnClickListener {
            viewModel.setWashType(EnumWashType.DELICATE)
        }
        binding.cardCold.setOnClickListener {
            viewModel.setWashType(EnumWashType.COLD)
        }
        binding.cardWarm.setOnClickListener {
            viewModel.setWashType(EnumWashType.WARM)
        }
        binding.cardHot.setOnClickListener {
            viewModel.setWashType(EnumWashType.HOT)
        }
        binding.cardSuper.setOnClickListener {
            viewModel.setWashType(EnumWashType.SUPER_WASH)
        }

        binding.cardButtonClose.setOnClickListener {
            finish()
        }
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    viewModel.save()
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(MACHINE_TYPE_FILTER, MachineTypeFilter(
                            it.data.serviceRef.machineType,
                            it.data.serviceRef.serviceType
                        ))
                    })
                    ShopSetupSyncWorker.enqueue(this)
                    viewModel.resetState()
                    finish()
                }
//                is DataState.DeleteSuccess -> {
//                    ShopSetupSyncWorker.enqueue(this)
//                    viewModel.resetState()
//                    finish()
//                }
//                is DataState.RequestExit -> {
//                    confirmExit(it.promptPass)
//                    viewModel.resetState()
//                }

                else -> {}
            }
        })
    }
}