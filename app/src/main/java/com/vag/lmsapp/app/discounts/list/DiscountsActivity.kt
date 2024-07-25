package com.vag.lmsapp.app.discounts.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.discounts.edit.DiscountAddEditActivity
import com.vag.lmsapp.databinding.ActivityDiscountsBinding
import com.vag.lmsapp.room.entities.EntityDiscount
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscountsActivity : FilterActivity() {
    private lateinit var binding: ActivityDiscountsBinding
    private val viewModel: DiscountsViewModel by viewModels()
    private val adapter = Adapter<EntityDiscount>(R.layout.recycler_item_discounts_full)
    override var enableAdvancedFilter: Boolean = false

    override var filterHint = "Search Discounts"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discounts)
        super.onCreate(savedInstanceState)

        binding.recycler.adapter = adapter

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
        adapter.onItemClick = {
            openAddEdit(it)
        }
        binding.buttonCreateNew.setOnClickListener {
            openAddEdit(null)
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
    }

    private fun openAddEdit(item: EntityDiscount?) {
        val intent = Intent(this, DiscountAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, item?.id.toString())
        }
        addEditLauncher.launch(intent)
    }
}