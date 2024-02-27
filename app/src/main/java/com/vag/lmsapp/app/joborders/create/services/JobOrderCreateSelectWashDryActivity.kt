package com.vag.lmsapp.app.joborders.create.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.shared_ui.CreateJobOrderModifyQuantityBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectWashDryBinding
import com.vag.lmsapp.model.EnumMachineType
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectWashDryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderCreateSelectWashDryBinding

    private val viewModel: AvailableServicesViewModel by viewModels()

    private lateinit var modifyQuantityDialog: CreateJobOrderModifyQuantityBottomSheetFragment

    private val adapter = AvailableServicesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_wash_dry)
        binding.lifecycleOwner = this

        binding.inclMachines.recyclerAvailableServices.adapter = adapter

        subscribeEvents()
    }

    private fun openSelectedItem() {
        val tab = binding.tabMachineType.tabMachineType
        intent.getParcelableExtra<MenuServiceItem>(JobOrderCreateActivity.ITEM_PRESET_EXTRA).let {
            if(it != null) {
                itemClick(it)
            } else {
                viewModel.setMachineType(EnumMachineType.REGULAR_WASHER.toString())
            }

            val index = EnumMachineType.values().indexOf(
                it?.machineType ?: EnumMachineType.REGULAR_WASHER
            )

            tab.getTabAt(index)?.select()
        }
    }

    private fun itemClick(serviceItem: MenuServiceItem) {
        requestModifyQuantity(
            QuantityModel(serviceItem.serviceRefId, serviceItem.abbr(), serviceItem.quantity, QuantityModel.TYPE_SERVICE)
        )
    }

    private fun requestModifyQuantity(quantityModel: QuantityModel) {
        modifyQuantityDialog = CreateJobOrderModifyQuantityBottomSheetFragment.getInstance(quantityModel).apply {
            onOk = {
                viewModel.putService(it)
            }
            onItemRemove = {
                viewModel.removeService(it)
            }
        }
        modifyQuantityDialog.show(supportFragmentManager, this.toString())
    }

    private fun subscribeEvents() {
        adapter.onItemClick = { itemClick(it) }

        binding.tabMachineType.tabMachineType.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setMachineType(tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewModel.setMachineType(tab?.text.toString())
            }
        })
        binding.buttonOk.setOnClickListener {
            viewModel.prepareSubmit()
        }
        binding.buttonCancel.setOnClickListener {
            finish()
        }
        viewModel.selectedTab.observe(this, Observer {
            binding.inclMachines.viewModel = it
            adapter.setData(viewModel.getServices(it))
        })
        viewModel.availableServices.observe(this, Observer {
            viewModel.setPreSelectedServices(intent.getParcelableArrayListExtra<MenuServiceItem>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.toList())
            openSelectedItem()
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is AvailableServicesViewModel.DataState.UpdateService -> {
                    adapter.updateItem(it.serviceItem)
                    viewModel.resetState()
                }
                is AvailableServicesViewModel.DataState.Submit -> {
                    submit(it.selectedItems)
                    viewModel.resetState()
                }
                is AvailableServicesViewModel.DataState.InvalidOperation -> {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun submit(list: List<MenuServiceItem>) {
        setResult(RESULT_OK, Intent().apply {
            action = intent.action
            putParcelableArrayListExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, ArrayList(list))
        })
        finish()
    }
}