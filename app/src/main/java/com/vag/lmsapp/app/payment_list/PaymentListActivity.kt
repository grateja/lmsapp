package com.vag.lmsapp.app.payment_list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.joborders.payment.preview.PaymentPreviewActivity
import com.vag.lmsapp.app.payment_list.advanced_filter.PaymentListFilterBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityPaymentListBinding
import com.vag.lmsapp.room.entities.EntityJobOrderPaymentListItem
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class PaymentListActivity : FilterActivity() {
    private val viewModel: PaymentListViewModel by viewModels()
    private lateinit var binding: ActivityPaymentListBinding
    private val adapter = Adapter<EntityJobOrderPaymentListItem>(R.layout.recycler_item_job_order_payment_list_item)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_list)
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerPaymentList.adapter = adapter

        subscribeListeners()
        subscribeEvents()

        intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER).let {
            viewModel.setDateFilter(it)
            viewModel.filter(true)
        }
    }

    override var filterHint: String = "Enter OR Number or Customer name"

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }

    private fun openPayment(paymentId: UUID, customerId: UUID) {
        val intent = Intent(this, PaymentPreviewActivity::class.java).apply {
            putExtra(PAYMENT_ID, paymentId.toString())
            putExtra(CUSTOMER_ID, customerId.toString())
        }
        addEditLauncher.launch(intent)
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
        adapter.onItemClick = {
            openPayment(it.id, it.customerId)
        }
        addEditLauncher.onOk = {
            viewModel.filter(true)
        }
    }

    override fun onAdvancedSearchClick() {
        super.onAdvancedSearchClick()
        viewModel.showAdvancedFilter()
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
                    PaymentListFilterBottomSheetFragment.newInstance(it.advancedFilter).apply{
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