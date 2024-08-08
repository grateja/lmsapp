package com.vag.lmsapp.app.machines.usage

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.ActivityMachineUsageBinding
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MachineUsageActivity : FilterActivity() {
    private val viewModel: MachineUsageViewModel by viewModels()
    private lateinit var binding: ActivityMachineUsageBinding
    private val adapter = Adapter<EntityMachineUsageDetails>(R.layout.recycler_item_machine_usage_details)
    private lateinit var dateRangeDialog: BottomSheetDateRangePickerFragment
//    private val machineUsageFragment = MachineUsagePreviewBottomSheetFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_machine_usage)

        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerMachineUsage.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        window.statusBarColor = resources.getColor(R.color.color_code_machines, null)

        intent.getStringExtra(Constants.MACHINE_ID)?.toUUID()?.let {
            viewModel.setMachineId(it)
        }
        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER)?.let {
            viewModel.setDateFilter(it)
        }
        intent.getParcelableExtra<EnumMachineType>(Constants.MACHINE_TYPE)?.let {
            viewModel.setMachineType(it)
        }

        viewModel.filter(true)
    }

//    override fun onResume() {
//        super.onResume()
//
//        intent.getStringExtra(Constants.MACHINE_ID_EXTRA)?.toUUID()?.let {
//            viewModel.setMachineId(it)
//        }
//        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER)?.let {
//            viewModel.setDateFilter(it)
//        }
//
//        viewModel.filter(true)
//    }

    override var filterHint: String = "Search customer name"
//    override var toolbarBackground: Int = R.color.color_code_machines
    override var enableAdvancedFilter = false

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    private fun openDateFilter(dateFilter: DateFilter) {
        dateRangeDialog = BottomSheetDateRangePickerFragment.getInstance(dateFilter)
        dateRangeDialog.show(supportFragmentManager, null)
        dateRangeDialog.onOk = {
            viewModel.setDateFilter(it)
            viewModel.filter(true)
        }
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        adapter.onItemClick = {
            viewModel.setCurrentItem(it)
//            machineUsageFragment.show(supportFragmentManager, null)
        }
        binding.cardDateRange.setOnClickListener {
            viewModel.showDatePicker()
        }
        binding.buttonClearDateFilter.setOnClickListener {
            viewModel.clearDates()
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

                else -> {}
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is MachineUsageViewModel.NavigationState.OpenDateFilter -> {
                    openDateFilter(it.dateFilter)
                    viewModel.clearState()
                }

                else -> {}
            }
        })
        viewModel.dateFilter.observe(this, Observer {
            viewModel.filter(true)
        })
        viewModel.machineType.observe(this, Observer {
            viewModel.filter(true)
        })
    }
}