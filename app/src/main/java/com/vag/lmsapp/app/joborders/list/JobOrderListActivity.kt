package com.vag.lmsapp.app.joborders.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.customer.JobOrderCreateSelectCustomerActivity
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.ActivityJobOrderListBinding
import com.vag.lmsapp.model.JobOrderAdvancedFilter
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.viewmodels.ListViewModel
import com.google.android.material.tabs.TabLayout
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewBottomSheetFragment
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderListActivity : FilterActivity() {
    companion object {
        const val ADVANCED_FILTER = "advanced_filter"
    }

    private lateinit var binding: ActivityJobOrderListBinding
    private val viewModel: JobOrderListViewModel by viewModels()
    private val viewModelPreview: JobOrderPreviewViewModel by viewModels()

    override var filterHint = "Search customer name or CRN"
    override var toolbarBackground: Int = R.color.color_code_job_order

    private val adapter = Adapter<JobOrderListItem>(R.layout.recycler_item_job_order_list_item)
    private lateinit var dateRangeDialog: BottomSheetDateRangePickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "Job Orders"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_list)
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerJobOrderList.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tabRegularAndTrashed.apply {
            this.addTab(this.newTab().setText("Active"))
            this.addTab(this.newTab().setText("Deleted"))
            this.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.setView(tab?.position == 0)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) { }

                override fun onTabReselected(tab: TabLayout.Tab?) { }
            })
        }

        subscribeEvents()
        subscribeListeners()

//        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER)?.let {
//            viewModel.setDateRange(it)
//        }
//        intent.getParcelableExtra<EnumJoFilterBy>(FILTER_BY)?.let {
//            viewModel.setFilterBy(it)
//        }

        intent.extras?.getParcelable<JobOrderAdvancedFilter>(ADVANCED_FILTER).let {
            viewModel.setAdvancedFilter(it ?: JobOrderAdvancedFilter())
        }
    }

    override fun onAdvancedSearchClick() {
        viewModel.showAdvancedFilter()
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            JobOrderPreviewBottomSheetFragment.newInstance(false, it.id).show(supportFragmentManager, null)
//            val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
//                action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
//                putExtra(JobOrderCreateActivity.JOB_ORDER_ID, it.id.toString())
//            }
//            addEditLauncher.launch(intent)
        }
        addEditLauncher.onOk = {
            viewModel.filter(true)
        }
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
//        binding.cardAddNew.setOnClickListener {
//            createNewJo()
////            selectCustomer()
//        }
        binding.buttonCreateNewJobOrder.setOnClickListener {
            createNewJo()
        }
        binding.cardAdvancedOptions.setOnClickListener {
            viewModel.showAdvancedFilter()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.filter(true)
        }
    }

    private fun createNewJo() {
        val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
            action = JobOrderCreateActivity.ACTION_LOAD_EMPTY_JOB_ORDER
        }
        addEditLauncher.launch(intent)
    }

//    private fun selectCustomer() {
//        val intent = Intent(this, JobOrderCreateSelectCustomerActivity::class.java)
//        startActivity(intent)
//    }

    private fun openDateFilter(dateFilter: DateFilter) {
        dateRangeDialog = BottomSheetDateRangePickerFragment.getInstance(dateFilter)
        dateRangeDialog.show(supportFragmentManager, null)
        dateRangeDialog.onOk = {
//            viewModel.setDateRange(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.result.observe(this, Observer {
            println(it)
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is ListViewModel.DataState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    binding.swipeRefresh.isRefreshing = false
                    viewModel.clearState()
                }

                else -> {}
            }
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is JobOrderListViewModel.NavigationState.OpenDateFilter -> {
                    openDateFilter(it.dateFilter)
                    viewModel.clearState()
                }
                is JobOrderListViewModel.NavigationState.ShowAdvancedFilter -> {
                    println("advanced filter date")
                    println(it.advancedFilter.dateFilter)
                    val fragment = JobOrderListAdvancedFilterFragment.getInstance(it.advancedFilter)
                    fragment.show(supportFragmentManager, null)
                    fragment.onOk = {
                        viewModel.setAdvancedFilter(it)
                    }
                    viewModel.clearState()
                }

                else -> {}
            }
        })

        viewModel.filterParams.observe(this, Observer {
            viewModel.filter(true)
            println("awesome")
        })

        viewModelPreview.navigationState.observe(this, Observer {
            when(it) {
                is JobOrderPreviewViewModel.NavigationState.RequireRefresh -> {
                    viewModel.filter(true)
                    viewModelPreview.resetState()
                }

                else -> {}
            }
        })

//        viewModel.sortDirection.observe(this, Observer {
//            viewModel.filter(true)
//        })
//
//        viewModel.orderBy.observe(this, Observer {
//            viewModel.filter(true)
//        })
//
//        viewModel.paymentStatus.observe(this, Observer {
//            viewModel.filter(true)
//        })
//
//        viewModel.filterBy.observe(this, Observer {
//            viewModel.filter(true)
//        })
//
//        viewModel.dateFilter.observe(this, Observer {
//            viewModel.filter(true)
//        })
//        viewModel.includeVoid.observe(this, Observer {
//            viewModel.filter(true)
//        })
    }
}