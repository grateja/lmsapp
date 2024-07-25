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

    override var filterHint = "Search Expenses Remarks"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expenses)

        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerExpenses.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        intent.getParcelableExtra<DateFilter>(Constants.DATE_RANGE_FILTER).let {
            viewModel.setDateFilter(it)
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
            viewModel.filter(true)
        }
    }

    private fun openAddEdit(item: ExpenseItemFull?) {
        val intent = Intent(this, ExpenseAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, item?.expense?.id.toString())
        }
        addEditLauncher.launch(intent)
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
                    ExpensesAdvancedFilterBottomSheetFragment.newInstance(it.advancedFilter).apply{
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