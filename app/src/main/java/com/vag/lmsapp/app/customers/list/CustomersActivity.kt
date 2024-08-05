package com.vag.lmsapp.app.customers.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.customers.create.CustomerAddEditBottomSheetFragment
import com.vag.lmsapp.app.customers.list.advanced_filter.CustomerListFilterBottomSheetFragment
import com.vag.lmsapp.app.customers.list.advanced_filter.CustomersAdvancedFilter
import com.vag.lmsapp.app.customers.preview.CustomerPreviewActivity
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.ActivityCustomersBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.toFormattedString
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomersActivity : FilterActivity() {
    private lateinit var binding: ActivityCustomersBinding
    private val viewModel: CustomersViewModel by viewModels()
    private val adapter = Adapter<CustomerListItem>(R.layout.recycler_item_customers_list_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customers)
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recycler.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER).let { dateFilter ->
            viewModel.setDateFilter(dateFilter)
            viewModel.filter(true)
        }
    }

    private fun subscribeEvents() {
        addEditLauncher.onOk = {
            viewModel.filter(true)
        }
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        adapter.onItemClick = {
            val intent = Intent(this, CustomerPreviewActivity::class.java).apply {
                putExtra(CustomerPreviewActivity.CUSTOMER_ID_EXTRA, it.customer.id.toString())
            }
            addEditLauncher.launch(intent)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.filter(true)
        }
        binding.buttonCreateNew.setOnClickListener {
            CustomerAddEditBottomSheetFragment.newInstance(null, null, false).apply {
                onOk = {
                    viewModel.filter(true)
                }
            }.show(supportFragmentManager, null)
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
                    binding.swipeRefresh.isRefreshing = false
                    viewModel.clearState()
                }
                is FilterState.ShowAdvancedFilter -> {
                    CustomerListFilterBottomSheetFragment.newInstance(it.advancedFilter).apply {
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
    }

    override fun onAdvancedSearchClick() {
        viewModel.showAdvancedFilter()
    }


    override var filterHint = "Search customer name or CRN"

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
        viewModel.filter(true)
    }
}