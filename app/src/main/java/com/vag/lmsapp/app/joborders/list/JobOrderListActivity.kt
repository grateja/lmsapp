package com.vag.lmsapp.app.joborders.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.databinding.ActivityJobOrderListBinding
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilter
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilterFragment
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewBottomSheetFragment
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewViewModel
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.util.Constants.Companion.ADVANCED_FILTER
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderListActivity : FilterActivity() {

    private lateinit var binding: ActivityJobOrderListBinding
    private val viewModel: JobOrderListViewModel by viewModels()
    private val viewModelPreview: JobOrderPreviewViewModel by viewModels()

    override var filterHint = "Search customer name or CRN"

    private val adapter = Adapter<JobOrderListItem>(R.layout.recycler_item_job_order_list_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_list)
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerJobOrderList.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupTab()
        subscribeEvents()
        subscribeListeners()

        intent.extras?.getParcelable<JobOrderListAdvancedFilter>(ADVANCED_FILTER).let {
            viewModel.setAdvancedFilter(it ?: JobOrderListAdvancedFilter())
            viewModel.filter(true)
        }
    }

    private fun setupTab() {
        binding.paymentStatusTab.tabItemPaymentStatusAll.setOnClickListener {
            viewModel.setPaymentStatus(EnumPaymentStatus.ALL)
            viewModel.filter(true)
        }
        binding.paymentStatusTab.tabItemPaymentStatusPaid.setOnClickListener {
            viewModel.setPaymentStatus(EnumPaymentStatus.PAID)
            viewModel.filter(true)
        }
        binding.paymentStatusTab.tabItemPaymentStatusUnpaid.setOnClickListener {
            viewModel.setPaymentStatus(EnumPaymentStatus.UNPAID)
            viewModel.filter(true)
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
        }
        addEditLauncher.onOk = {
            viewModel.filter(true)
        }
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        binding.buttonCreateNew.setOnClickListener {
            createNewJo()
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

    private fun subscribeListeners() {
        viewModel.result.observe(this, Observer {
            println(it)
        })
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    binding.swipeRefresh.isRefreshing = false
                    viewModel.clearState()
                }

                is FilterState.ShowAdvancedFilter -> {
                    JobOrderListAdvancedFilterFragment.newInstance(it.advancedFilter).apply {
                        onOk = {
                            viewModel.setAdvancedFilter(it)
                            viewModel.filter(true)
                        }
                    }.show(supportFragmentManager, null)
                    viewModel.clearState()
                }

                else -> {}
            }
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
    }
}