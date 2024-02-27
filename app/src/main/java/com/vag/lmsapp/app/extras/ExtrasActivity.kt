package com.vag.lmsapp.app.extras

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.extras.edit.ExtrasAddEditActivity
import com.vag.lmsapp.databinding.ActivityExtrasBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExtrasActivity : FilterActivity() {
    private lateinit var binding: ActivityExtrasBinding
    private val viewModel: ExtrasViewModel by viewModels()
    private val adapter = Adapter<ExtrasItemFull>(R.layout.recycler_item_extras_full)
//    private val addEditLauncher = ActivityLauncher(this)

    override var filterHint = "Search Expenses Remarks"
    override var toolbarBackground: Int = R.color.teal_700

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
        binding.buttonCreateNew.setOnClickListener {
            openAddEdit(null)
        }
        adapter.onItemClick = {
            openAddEdit(it)
        }
        addEditLauncher.onOk = {
            val expenseId = it.data?.getStringExtra(CrudActivity.ENTITY_ID).toUUID()
        }
    }

    private fun openAddEdit(item: ExtrasItemFull?) {
        val intent = Intent(this, ExtrasAddEditActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, item?.extras?.id.toString())
        }
        addEditLauncher.launch(intent)
    }

    private fun subscribeListeners() {
        viewModel.items.observe(this, Observer {
            adapter.setData(it)
        })
    }
}