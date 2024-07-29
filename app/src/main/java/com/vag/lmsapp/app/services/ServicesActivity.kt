package com.vag.lmsapp.app.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.services.edit.AddEditServiceActivity
import com.vag.lmsapp.databinding.ActivityServicesBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.entities.EntityService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.app.services.preview.ServicePreviewBottomSheetFragment
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ServicesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServicesBinding
    private val viewModel: ServicesViewModel by viewModels()
    private val adapter = Adapter<EntityService>(R.layout.recycler_item_service)
    private val launcher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_services)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.inclMachines.recyclerAvailableServices.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        binding.tabMachineType.cardRegularWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardRegularDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.DRY)
        }
        binding.tabMachineType.cardTitanWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardTitanDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.DRY)
        }
//        binding.tabMachineType.tabMachineType.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewModel.selectTab(tab?.text.toString())
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) { }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                viewModel.selectTab(tab?.text.toString())
//            }
//        })
        adapter.onItemClick = {
            ServicePreviewBottomSheetFragment.newInstance(it.id).show(supportFragmentManager, null)
//            viewModel.openAddEdit(it.id)
        }
        binding.inclMachines.buttonCreateNew.setOnClickListener {
            viewModel.openAddEdit(null)
        }
        launcher.onOk = {
            val filter = it.data?.getParcelableExtra<MachineTypeFilter>(AddEditServiceActivity.MACHINE_TYPE_FILTER)
            viewModel.setMachineType(filter?.machineType, filter?.serviceType)
//            val index = EnumMachineType.values().indexOf(
//                machineType ?: EnumMachineType.REGULAR_WASHER
//            )
//
//            binding.tabMachineType.tabMachineType.getTabAt(index)?.select()
        }
    }

    private fun openAddEdit(serviceId: UUID?, filter: MachineTypeFilter?) {
        val intent = Intent(this, AddEditServiceActivity::class.java).apply {
            putExtra(ENTITY_ID, serviceId.toString())
            putExtra(AddEditServiceActivity.MACHINE_TYPE_FILTER, filter)
        }
        launcher.launch(intent)
    }

    private fun subscribeListeners() {
        viewModel.services.observe(this, Observer {
            adapter.setData(it)
        })
//        viewModel.selectedTab.observe(this, Observer {
//            binding.inclMachines.viewModel = it
//        })
        viewModel.filter.observe(this, Observer {
            binding.inclMachines.machineTypeFilter = it
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is ServicesViewModel.DataState.OpenAddEdit -> {
                    openAddEdit(it.serviceId, it.filter)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}