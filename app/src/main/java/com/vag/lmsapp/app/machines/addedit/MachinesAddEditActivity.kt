package com.vag.lmsapp.app.machines.addedit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityMachinesAddEditBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachinesAddEditActivity : AppCompatActivity() {

    companion object {
        const val MACHINE_TYPE_FILTER = "filter"
    }

    private lateinit var binding: ActivityMachinesAddEditBinding
    private val viewModel: AddEditMachineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_machines_add_edit)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()

        binding.controls.viewModel = viewModel

        val machineId = intent.getStringExtra(Constants.MACHINE_ID).toString()
        val filter = intent.getParcelableExtra<MachineTypeFilter>(MACHINE_TYPE_FILTER)
        viewModel.get(machineId, filter)
    }

    private fun subscribeEvents() {
        binding.controls.cardButtonConfirm.setOnClickListener {
            viewModel.save()
        }
        binding.controls.cardButtonClose.setOnClickListener {
            finish()
        }

        binding.cardMachineType8kg.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR)
        }

        binding.cardMachineType12kg.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN)
        }

        binding.cardServiceTypeDry.setOnClickListener {
            viewModel.setServiceType(EnumServiceType.DRY)
        }

        binding.cardServiceTypeWash.setOnClickListener {
            viewModel.setServiceType(EnumServiceType.WASH)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    finish()
                    ShopSetupSyncWorker.enqueue(this)
                }
                else -> {}
            }
        })
    }
}