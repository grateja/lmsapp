package com.vag.lmsapp.app.customers.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.customers.create.CustomerAddEditBottomSheetFragment
import com.vag.lmsapp.app.customers.preview.CustomerPreviewActivity
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.ActivityCustomersBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomersActivity : FilterActivity() {
    private lateinit var binding: ActivityCustomersBinding
    private val viewModel: CustomersViewModel by viewModels()
    private val adapter = Adapter<CustomerListItem>(R.layout.recycler_item_customers_list_item)
    private lateinit var dateRangeDialog: BottomSheetDateRangePickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "Customers"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customers)
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recycler.adapter = adapter

        subscribeEvents()
        subscribeListeners()
    }

    override fun onResume() {
        super.onResume()
        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER)?.let { dateFilter ->
            viewModel.setDateRange(dateFilter)
        } ?: viewModel.filter(true)
    }

    private fun openDateFilter(dateFilter: DateFilter) {
        dateRangeDialog = BottomSheetDateRangePickerFragment.getInstance(dateFilter)
        dateRangeDialog.show(supportFragmentManager, null)
        dateRangeDialog.onOk = {
            viewModel.setDateRange(it)
        }
    }

    private fun subscribeEvents() {
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
        binding.cardDateRange.setOnClickListener {
            viewModel.showDatePicker()
        }
        binding.buttonClearDateFilter.setOnClickListener {
            viewModel.clearDates()
        }
        binding.buttonCreateNewCustomer.setOnClickListener {
            CustomerAddEditBottomSheetFragment.newInstance(null, null, false).apply {
                onOk = {
                    viewModel.filter(true)
                }
            }.show(supportFragmentManager, null)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is ListViewModel.DataState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                    binding.swipeRefresh.isRefreshing = false
                }

                else -> {}
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is CustomersViewModel.NavigationState.OpenDateFilter -> {
                    openDateFilter(it.dateFilter)
                    viewModel.clearState()
                }

                else -> {}
            }
        })
//        viewModel.orderBy.observe(this, Observer {
//            viewModel.filter(true)
//        })
//        viewModel.sortDirection.observe(this, Observer {
//            viewModel.filter(true)
//        })
        viewModel.dateFilter.observe(this, Observer {
            viewModel.filter(true)
        })
    }


    override var filterHint = "Search customer name or CRN"
    override var toolbarBackground: Int = R.color.color_code_customers

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }
}