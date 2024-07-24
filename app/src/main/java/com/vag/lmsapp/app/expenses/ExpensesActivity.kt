package com.vag.lmsapp.app.expenses

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.expenses.advanced_filter.ExpensesAdvancedFilter
import com.vag.lmsapp.app.expenses.advanced_filter.ExpensesAdvancedFilterBottomSheetFragment
import com.vag.lmsapp.app.expenses.edit.ExpenseAddEditActivity
import com.vag.lmsapp.app.shared_ui.BottomSheetDateRangePickerFragment
import com.vag.lmsapp.databinding.ActivityExpensesBinding
import com.vag.lmsapp.util.*
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpensesActivity : FilterActivity() {
    private lateinit var binding: ActivityExpensesBinding
    private val viewModel: ExpensesViewModel by viewModels()
    private val adapter = Adapter<ExpenseItemFull>(R.layout.recycler_item_expenses_full)
    private lateinit var dateRangeDialog: BottomSheetDateRangePickerFragment

    override var filterHint = "Search Expenses Remarks"
    override var toolbarBackground: Int = R.color.white

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expenses)

        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerExpenses.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscribeEvents()
        subscribeListeners()

        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER)?.let {
            viewModel.setFilterParams(
                ExpensesAdvancedFilter().apply {
                    dateFilter = it
                }
            )
        }
    }

    override fun onAdvancedSearchClick() {
        viewModel.showAdvancedFilter()
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        binding.buttonCreateNewExpense.setOnClickListener {
            openAddEdit(null)
        }
        adapter.onItemClick = {
            openAddEdit(it)
        }
        addEditLauncher.onOk = {
            val expenseId = it.data?.getStringExtra(CrudActivity.ENTITY_ID).toUUID()
        }
    }

    private fun openAddEdit(item: ExpenseItemFull?) {
        val intent = Intent(this, ExpenseAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, item?.expense?.id.toString())
        }
        addEditLauncher.launch(intent)
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
                    viewModel.clearState()
                }

                else -> {}
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is ExpensesViewModel.NavigationState.ShowAdvancedFilter -> {
                    ExpensesAdvancedFilterBottomSheetFragment.newInstance(it.filter).apply{
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
}