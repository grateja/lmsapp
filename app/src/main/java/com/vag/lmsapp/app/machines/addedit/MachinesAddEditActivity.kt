package com.vag.lmsapp.app.machines.addedit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityMachinesAddEditBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.worker.ShopSetupSyncWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachinesAddEditActivity : AppCompatActivity() {

    companion object {
        const val MACHINE_TYPE_EXTRA = "machineType"
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

        val machineId = intent.getStringExtra(Constants.MACHINE_ID_EXTRA).toString()
        val machineType = EnumMachineType.fromId(intent.getIntExtra(MACHINE_TYPE_EXTRA, 1))

        machineType?.let {
            println("machine id")
            println(machineId)
            println("machine type")
            println(it)
            viewModel.get(machineId, it)
        }
    }

    private fun subscribeEvents() {
        binding.buttonSave.setOnClickListener {
            viewModel.save()
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