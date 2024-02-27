package com.vag.lmsapp.app.joborders.create.customer

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.customers.create.CustomerAddEditBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity.Companion.CUSTOMER_ID
import com.vag.lmsapp.app.joborders.unpaid.prompt.JobOrdersUnpaidPromptActivity
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectCustomerBinding
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.showConfirmationDialog
import com.vag.lmsapp.util.toUUID
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class JobOrderCreateSelectCustomerActivity : FilterActivity() {
    companion object {
        const val CUSTOMER = "customer"
    }
    private val viewModel: SelectCustomerViewModel by viewModels()
    private lateinit var binding: ActivityJobOrderCreateSelectCustomerBinding
    private lateinit var customerModal: CustomerAddEditBottomSheetFragment
    private val customersAdapter = CustomersAdapterMinimal()

    override var filterHint = "Search Customer Name/CRN"
    override var toolbarBackground: Int = R.color.color_code_customers
    override var enableAdvancedFilter = false
    override var enableAddButton = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_customer)
        super.onCreate(savedInstanceState)
        title = "Search Customer"
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerCustomersMinimal.adapter = customersAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscribeListeners()
        subscribeEvents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
        intent.getStringExtra(CUSTOMER_ID).toUUID()?.let {
            viewModel.setCurrentCustomerId(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is ListViewModel.DataState.LoadItems -> {
                    if(it.reset) {
                        customersAdapter.setData(it.items)
                    } else {
                        customersAdapter.addItems(it.items)
                    }
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }

    override fun onAddButtonClick() {
        editCustomer(null)
    }

    private fun subscribeEvents() {
//        binding.buttonCreateNew.setOnClickListener {
//            editCustomer(null)
//            customerModal = AddEditCustomerFragment.getInstance(null)
//            customerModal.show(supportFragmentManager, "KEME")
//            customerModal.onOk = {
//                openCreateJobOrderActivity(it!!)
//            }
//        }
        customersAdapter.onItemClick = {
            selectCustomer(it)
//            open(it)
        }
        customersAdapter.onEdit = {
            editCustomer(it.id)
        }
        customersAdapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
    }

    private fun selectCustomer(customer: CustomerMinimal) {
        val intent = Intent()
        if(customer.hasUnpaidJoToday != null) {
            showConfirmationDialog("Unpaid job order detected!", "The selected customer has an unpaid job order created today. Would you like to load it instead?") {
                intent.action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
                intent.putExtra(JobOrderCreateActivity.JOB_ORDER_ID, customer.hasUnpaidJoToday.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        } else if(customer.unpaid > 0) {
//            viewModel.checkIfCustomerCanCreateNewJobOrder(customer)
            openUnpaidJobOrderPrompt(customer)
        } else {
            intent.action = JobOrderCreateActivity.ACTION_SELECT_CUSTOMER
            intent.putExtra(CUSTOMER_ID, customer.id.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

//    private fun open(customer: CustomerMinimal) {
//        if(customer.unpaid != null && customer.unpaid!! > 0 && customer.lastJobOrder?.isToday() != true) {
//            openUnpaidJobOrderPrompt(customer)
//        } else {
//            openCreateJobOrderActivity(customer)
//        }
//    }

    private fun editCustomer(customerId: UUID?) {
        customerModal = CustomerAddEditBottomSheetFragment.getInstance(customerId, searchBar?.query.toString(), false)
        customerModal.show(supportFragmentManager, "KEME")
        customerModal.onOk = {
            it?.let {
                selectCustomer(it)
            }
        }
    }

//    private fun openCreateJobOrderActivity(customer: CustomerMinimal) {
//        val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
//            action = JobOrderCreateActivity.ACTION_LOAD_BY_CUSTOMER_ID
//            putExtra(JobOrderCreateActivity.CUSTOMER_EXTRA, customer.id.toString())
//        }
//        startActivity(intent)
//    }

    private fun openUnpaidJobOrderPrompt(customer: CustomerMinimal) {
        val intent = Intent(this, JobOrdersUnpaidPromptActivity::class.java).apply {
            putExtra(JobOrdersUnpaidPromptActivity.CUSTOMER_EXTRA, customer)
        }
        startActivity(intent)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }
}