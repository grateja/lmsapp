package com.vag.lmsapp.app.extras.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.extras.edit.ExtrasAddEditActivity
import com.vag.lmsapp.app.extras.list.advanced_filter.ExtrasAdvancedFilterBottomSheetFragment
import com.vag.lmsapp.app.extras.preview.ExtrasPreviewBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityExtrasBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExtrasActivity : FilterActivity() {
    private lateinit var binding: ActivityExtrasBinding
    private val viewModel: ExtrasViewModel by viewModels()
    private val adapter = Adapter<ExtrasItemFull>(R.layout.recycler_item_extras_full)
    override var enableAdvancedFilter = true

    override var filterHint = "Search Expenses Remarks"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_extras)
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerExpenses.adapter = adapter

        subscribeEvents()
        subscribeListeners()
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
        binding.buttonCreateNewExtraService.setOnClickListener {
            openAddEdit(null)
        }
        adapter.onItemClick = {
            ExtrasPreviewBottomSheetFragment.newInstance(it.extras.id).apply {
                onOk = {
                    viewModel.filter(true)
                }
            }.show(supportFragmentManager, null)
//            openAddEdit(it)
        }
        addEditLauncher.onOk = {
            viewModel.filter(true)
        }
    }

    private fun openAddEdit(item: ExtrasItemFull?) {
        val intent = Intent(this, ExtrasAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, item?.extras?.id.toString())
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
                    ExtrasAdvancedFilterBottomSheetFragment.newInstance(it.advancedFilter).apply{
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
}