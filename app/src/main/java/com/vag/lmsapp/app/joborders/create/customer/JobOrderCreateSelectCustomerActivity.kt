package com.vag.lmsapp.app.joborders.create.customer

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.customers.create.CustomerAddEditBottomSheetFragment
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity
import com.vag.lmsapp.app.joborders.unpaid.prompt.BottomSheetJobOrderPaymentPromptFragment
import com.vag.lmsapp.app.joborders.unpaid.prompt.JobOrdersUnpaidPromptActivity.Companion.OPEN_PAYMENT_ACTION
import com.vag.lmsapp.app.joborders.unpaid.prompt.JobOrdersUnpaidPromptViewModel
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectCustomerBinding
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.FilterActivity
import com.vag.lmsapp.util.FilterState
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
    private val unpaidPromptViewModel: JobOrdersUnpaidPromptViewModel by viewModels()
    private lateinit var binding: ActivityJobOrderCreateSelectCustomerBinding
    private lateinit var customerModal: CustomerAddEditBottomSheetFragment
    private val customersAdapter = Adapter<CustomerMinimal>(R.layout.recycler_item_customer_minimal)

    override var filterHint = "Search Customer Name/CRN"
//    override var toolbarBackground: Int = R.color.color_code_customers
    override var enableAdvancedFilter = false
//    override var enableAddButton = true

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

        intent.getStringExtra(CUSTOMER_ID).toUUID()?.let {
            viewModel.setCurrentCustomerId(it)
        }

        viewModel.filter(true)
    }

    private fun subscribeListeners() {
//        viewModel.dataState.observe(this, Observer {
//            when(it) {
//                is ListViewModel.DataState.LoadItems -> {
//                    if(it.reset) {
//                        customersAdapter.setData(it.items)
//                    } else {
//                        customersAdapter.addItems(it.items)
//                    }
//                    viewModel.clearState()
//                }
//
//                else -> {}
//            }
//        })
        unpaidPromptViewModel.dataState.observe(this, Observer {
            when(it) {
                is JobOrdersUnpaidPromptViewModel.DataState.OpenPayment -> {
                    val intent = Intent(this, JobOrderPaymentActivity::class.java).apply {
                        action = OPEN_PAYMENT_ACTION
                        putExtra(CUSTOMER_ID, it.customerId.toString())
                    }
                    startActivity(intent)
//                    openPayment(it.customerId)
//                    val intent = Intent(OPEN_PAYMENT_ACTION).apply {
//                        putExtra(CUSTOMER_ID, it.customerId.toString())
//                    }
//                    setResult(RESULT_OK, intent)
                    unpaidPromptViewModel.resetState()
                }
                is JobOrdersUnpaidPromptViewModel.DataState.OpenJobOrder -> {
//                    openCreateJobOrderActivity(it.customer)
                    selectCustomer(it.customer.id)
                    unpaidPromptViewModel.resetState()
                }

                else -> {}
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is SelectCustomerViewModel.NavigationState.SelectCustomer -> {
                    val intent = Intent(JobOrderCreateActivity.ACTION_SELECT_CUSTOMER).apply {
                        putExtra(CUSTOMER_ID, it.customerId.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                    viewModel.resetState()
                }

                is SelectCustomerViewModel.NavigationState.EditCustomer -> {
                    editCustomer(it.customerId)
                    viewModel.resetState()
                }

                is SelectCustomerViewModel.NavigationState.SelectJobOrder -> {
                    val intent = Intent(JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID).apply {
                        putExtra(JOB_ORDER_ID, it.jobOrderId.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
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

//    override fun onAddButtonClick() {
//        editCustomer(null)
//    }

    private fun subscribeEvents() {
        binding.buttonCreateNew.setOnClickListener {
            editCustomer(null)
//            customerModal = CustomerAddEditBottomSheetFragment.newInstance(null, null, true)
//            customerModal.show(supportFragmentManager, "KEME")
//            customerModal.onOk = {
//                previewCustomer(it?.id)
//            }
        }
        customersAdapter.onItemClick = {
            previewCustomer(it.id)
        }
//        customersAdapter.onEdit = {
//            editCustomer(it.id)
//        }
        customersAdapter.onScrollAtTheBottom = {
            viewModel.loadMore()
        }
    }

    private fun previewCustomer(customerId: UUID) {
        viewModel.checkCustomer(customerId)
        SelectCustomerPreviewBottomSheetFragment().show(supportFragmentManager, null)
    }

//    private fun checkCustomer(customer: CustomerMinimal) {
//        val intent = Intent()
//        if(customer.hasUnpaidJoToday != null) {
//            showConfirmationDialog("Unpaid job order detected!", "The selected customer has an unpaid job order created today. Would you like to load it instead?") {
//                intent.action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
//                intent.putExtra(JOB_ORDER_ID, customer.hasUnpaidJoToday.toString())
//                setResult(RESULT_OK, intent)
//                finish()
//            }
//        } else if(customer.unpaid > 0) {
//            openUnpaidJobOrderPrompt(customer)
//        } else {
//            selectCustomer(customer.id)
//        }
//    }

    private fun selectCustomer(customerId: UUID) {
        intent.action = JobOrderCreateActivity.ACTION_SELECT_CUSTOMER
        intent.putExtra(CUSTOMER_ID, customerId.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

//    private fun open(customer: CustomerMinimal) {
//        if(customer.unpaid != null && customer.unpaid!! > 0 && customer.lastJobOrder?.isToday() != true) {
//            openUnpaidJobOrderPrompt(customer)
//        } else {
//            openCreateJobOrderActivity(customer)
//        }
//    }

    private fun editCustomer(customerId: UUID?) {
        customerModal = CustomerAddEditBottomSheetFragment.newInstance(customerId, searchBar?.query.toString(), false)
        customerModal.show(supportFragmentManager, "KEME")
        customerModal.onOk = {
            it?.let {
                previewCustomer(it.id)
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
        unpaidPromptViewModel.setCustomer(customer)
        BottomSheetJobOrderPaymentPromptFragment().show(supportFragmentManager, null)
//        val intent = Intent(this, JobOrdersUnpaidPromptActivity::class.java).apply {
//            putExtra(JobOrdersUnpaidPromptActivity.CUSTOMER_EXTRA, customer)
//        }
//        startActivity(intent)
    }

    override fun onQuery(keyword: String?) {
        viewModel.setKeyword(keyword)
    }
}