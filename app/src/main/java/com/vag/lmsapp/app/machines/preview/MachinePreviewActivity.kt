package com.vag.lmsapp.app.machines.preview

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.machines.addedit.MachinesAddEditActivity
import com.vag.lmsapp.app.machines.machine_selector.MachineSelectorBottomSheetFragment
import com.vag.lmsapp.app.machines.ping.PingActivity
import com.vag.lmsapp.app.machines.preview.machine_usages.MachineUsageAdvancedFilterBottomSheetFragment
import com.vag.lmsapp.app.machines.preview.machine_usages.MachineUsagePreviewBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityMachinePreviewBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.Constants.Companion.MACHINE_ID
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.showDeleteConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MachinePreviewActivity : FilterActivity() {
    companion object {
        const val AUTH_CODE_EDIT = 1
        const val AUTH_CODE_DELETE = 2

        const val MACHINE_TYPE_EXTRA = "machine_type_extra"
        const val SERVICE_TYPE_EXTRA = "service_type_extra"
    }
    private lateinit var binding: ActivityMachinePreviewBinding
    private val viewModel: MachinePreviewViewModel by viewModels()
    override var filterHint: String = "Search customer name or job order number"

    private val adapter = Adapter<EntityMachineUsageDetails>(R.layout.recycler_item_machine_usage_details)
    private val authLauncher = AuthLauncherActivity(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_machine_preview)
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recycler.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        val machineId = intent.getStringExtra(MACHINE_ID).toUUID()
        val serviceType = intent.getParcelableExtra<EnumServiceType>(SERVICE_TYPE_EXTRA)
        val machineType = intent.getParcelableExtra<EnumMachineType>(MACHINE_TYPE_EXTRA)
        val dateFilter = intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER)

        viewModel.setMachineType(machineType)
        viewModel.setServiceType(serviceType)
        viewModel.setMachineId(machineId)
        viewModel.setDateFilter(dateFilter)
        viewModel.filter(true)
    }

    private fun subscribeEvents() {
        authLauncher.onOk = { _, code ->
            when(code) {
                AUTH_CODE_EDIT -> viewModel.openEdit()
                AUTH_CODE_DELETE -> viewModel.deleteMachine()
            }
        }

        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        adapter.onItemClick = {
            MachineUsagePreviewBottomSheetFragment.newInstance(it).show(supportFragmentManager, null)
        }

        binding.toolbar.setOnClickListener {
            viewModel.openMachineSelector()
        }
        binding.buttonEdit.setOnClickListener {
            authLauncher.launch(listOf(EnumActionPermission.MODIFY_SETTINGS_MACHINE), AUTH_CODE_EDIT)
        }
        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmationDialog(
                "Delete machine?",
                "Machine will be deleted and all usage history referenced to this machine.\nThis function cannot be undone!\nAre you sure you want to continue?"
            ) {
                authLauncher.launch(listOf(EnumActionPermission.MODIFY_SETTINGS_MACHINE), AUTH_CODE_DELETE)
            }
        }
        binding.buttonPing.setOnClickListener {
            viewModel.openPing()
        }
    }

    private fun subscribeListeners() {
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    viewModel.clearState()
                }

                is FilterState.ShowAdvancedFilter -> {
                    MachineUsageAdvancedFilterBottomSheetFragment.newInstance(it.advancedFilter).apply {
                        onOk = {
                            viewModel.setFilterParams(it)
                            viewModel.filter(true)
                        }
                    }.show(supportFragmentManager, null)
                    viewModel.clearState()
                }
                else -> {}
            }
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is MachinePreviewViewModel.NavigationState.OpenMachineSelector -> {
                    openMachineSelector(it.machineId)
                    viewModel.resetState()
                }

                is MachinePreviewViewModel.NavigationState.OpenEdit -> {
                    openEdit(it.machineId)
                    viewModel.resetState()
                }

                is MachinePreviewViewModel.NavigationState.OpenPing -> {
                    openPing(it.ipEnd)
                    viewModel.resetState()
                }

                is MachinePreviewViewModel.NavigationState.DeleteSuccess -> {
                    viewModel.resetState()
                    finish()
                }

                else -> {}
            }
        })
    }

    private fun openEdit(machineId: UUID?) {
        val intent = Intent(this, MachinesAddEditActivity::class.java).apply {
            putExtra(MACHINE_ID, machineId.toString())
        }
        startActivity(intent)
    }

    private fun openMachineSelector(machineId: UUID?) {
        MachineSelectorBottomSheetFragment.newInstance(machineId).apply {
            onOk = {
                viewModel.setMachineId(it)
                viewModel.filter(true)
            }
        }.show(supportFragmentManager, null)
    }

    private fun openPing(ipEnd: Int) {
        val intent = Intent(this, PingActivity::class.java).apply {
            putExtra(PingActivity.IP_END_EXTRA, ipEnd)
        }
        startActivity(intent)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    override fun onAdvancedSearchClick() {
        viewModel.openAdvancedFilter()
    }
}