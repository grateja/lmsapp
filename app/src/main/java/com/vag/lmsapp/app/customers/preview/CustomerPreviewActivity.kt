package com.vag.lmsapp.app.customers.preview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.customers.create.CustomerAddEditBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity.Companion.ACTION_LOAD_BY_CUSTOMER_ID
import com.vag.lmsapp.app.joborders.list.JobOrderListItem
import com.vag.lmsapp.app.joborders.list.JobOrderListViewModel
import com.vag.lmsapp.databinding.ActivityCustomerPreviewBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.toUUID
import com.vag.lmsapp.viewmodels.ListViewModel
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class CustomerPreviewActivity : AppCompatActivity() {
    companion object {
        const val CUSTOMER_ID_EXTRA = "customerId"
    }

    private val adapter = Adapter<JobOrderListItem>(R.layout.recycler_item_job_order_list_item)
    private lateinit var binding: ActivityCustomerPreviewBinding
    private val viewModel: CustomerPreviewViewModel by viewModels()
    private val jobOrdersViewModel: JobOrderListViewModel by viewModels()
    private val launcher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_preview)
        binding.viewModel = viewModel
        binding.jobOrderViewModel = jobOrdersViewModel
        binding.lifecycleOwner = this

        binding.recyclerJobOrderList.adapter = adapter

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(CUSTOMER_ID_EXTRA)?.toUUID()?.let {
            viewModel.load(it)
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            jobOrdersViewModel.filter(true)
//        }, 1000)
    }

    private fun subscribeEvents() {
        adapter.onScrollAtTheBottom = {
            jobOrdersViewModel.loadMore()
        }
        adapter.onItemClick = {
            val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
                action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
                putExtra(JobOrderCreateActivity.JOB_ORDER_ID, it.id.toString())
            }
            startActivity(intent)
        }
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    binding.cardCustomerName.visibility = View.VISIBLE
                } else -> {
                    binding.cardCustomerName.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun subscribeListeners() {
        viewModel.customer.observe(this, Observer {
            jobOrdersViewModel.setCustomerId(it.id)
            jobOrdersViewModel.filter(true)
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is CustomerPreviewViewModel.NavigationState.EditCustomer -> {
                    val edit = CustomerAddEditBottomSheetFragment.getInstance(it.customerId, null, false)
                    edit.show(supportFragmentManager, null)
                    viewModel.resetState()
                }
                is CustomerPreviewViewModel.NavigationState.PrepareNewJobOrder -> {
                    val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
                        action = ACTION_LOAD_BY_CUSTOMER_ID
                        putExtra(JobOrderCreateActivity.CUSTOMER_EXTRA, it.customerId.toString())
                    }
                    launcher.launch(intent)
                }

                else -> {}
            }
        })
        jobOrdersViewModel.dataState.observe(this, Observer {
            when(it) {
                is ListViewModel.DataState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
                        adapter.addItems(it.items)
                    }
                }

                else -> {}
            }
        })
    }
}