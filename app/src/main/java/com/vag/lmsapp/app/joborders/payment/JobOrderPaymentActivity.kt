package com.vag.lmsapp.app.joborders.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityJobOrderPaymentBinding
import com.vag.lmsapp.model.EnumPaymentMethod
import com.vag.lmsapp.services.JobOrderPaymentSyncService
import com.vag.lmsapp.util.*
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPaymentActivity : AppCompatActivity() {

    companion object {
//        const val CUSTOMER_ID = "customer_id"
//        const val PAYMENT_ID = "payment_id"
        const val SELECTED_JOB_ORDER_IDS = "jobOrderIds"
        const val AUTH_REQUEST_MODIFY_DATE_ACTION = "modifyDate"
        const val AUTH_REQUEST_SAVE = "save"
        const val ACTION_LOAD_BY_CUSTOMER = "loadByCustomer"
        const val ACTION_LOAD_BY_PAYMENT = "loadByPayment"
    }

    private lateinit var binding: ActivityJobOrderPaymentBinding
    private val viewModel: JobOrderPaymentViewModel by viewModels()
//    private val viewModelPreview: JobOrderPreviewViewModel by viewModels()
    private val fragment = BottomSheetJobOrderPaymentFragment()
//    private lateinit var cashlessModalFragment: PaymentJoCashlessModalFragment
    private val adapter = JobOrderListPaymentAdapter(false)

//    private val paymentMethodAdapter = OptionsAdapter(
//        R.layout.recycler_item_payment_method_option,
//        EnumPaymentMethod.values(),
//        R.color.color_code_payments
//    )

    private val authLauncher = ActivityLauncher(this)
    private val dateTimePicker: DateTimePicker by lazy {
        DateTimePicker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_payment)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerJobOrderPaymentMinimal.adapter = adapter

        window.statusBarColor = resources.getColor(R.color.color_code_payments, null)

        subscribeEvents()
        subscribeListeners()

        if(intent.action == ACTION_LOAD_BY_CUSTOMER) {
            intent.getStringExtra(CUSTOMER_ID).toUUID()?.let {
                viewModel.getUnpaidByCustomerId(it)
            }
        } else if(intent.action == ACTION_LOAD_BY_PAYMENT) {
            intent.getStringExtra(PAYMENT_ID).toUUID()?.let {
                viewModel.getPayment(it)
            }
        }
    }

    private fun auth(action: String, message: String) {
        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
            this.action = action
            putExtra(AuthActionDialogActivity.MESSAGE, message)
        }
        authLauncher.launch(intent)
    }

    private fun subscribeEvents() {
        binding.cardPaymentOptionCash.setOnClickListener {
            viewModel.setPaymentMethod(EnumPaymentMethod.CASH)
            fragment.show(supportFragmentManager, null)
        }
        binding.cardPaymentOptionCashless.setOnClickListener {
            viewModel.setPaymentMethod(EnumPaymentMethod.CASHLESS)
            fragment.show(supportFragmentManager, null)
        }
        binding.cardDatePaid.setOnClickListener {
            auth(AUTH_REQUEST_MODIFY_DATE_ACTION, "Modification of date paid requires authentication!")
        }
        adapter.onSelectionChange = {
            viewModel.selectItem(it)
        }
        adapter.onItemClick = {
            JobOrderPreviewBottomSheetFragment.newInstance(true, it.id).show(supportFragmentManager, null)
        }
        binding.buttonSave.setOnClickListener {
            viewModel.validate()
        }
        dateTimePicker.setOnDateTimeSelectedListener {
            viewModel.setDateTime(it)
        }
        authLauncher.onOk = { result ->
            when(result.data?.action) {
                AUTH_REQUEST_SAVE -> {
                    result.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
                        viewModel.save(it.userId)
                    }
                }
                AUTH_REQUEST_MODIFY_DATE_ACTION -> {
                    viewModel.requestModifyDateTime()
                }
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.customer.observe(this, Observer {
            title = "${it.name} - [${it.crn}]"
        })
        viewModel.payableJobOrders.observe(this, Observer {
            adapter.setData(it)
        })
        viewModel.amountToPay.observe(this, Observer {
        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is JobOrderPaymentViewModel.DataState.ValidationPassed -> {
                    auth(AUTH_REQUEST_SAVE, "Saving job order requires authentication!")
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.PaymentSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        action = intent.action
                        putExtra(PAYMENT_ID, it.payment.id.toString())
                        putExtra(SELECTED_JOB_ORDER_IDS, it.jobOrderIds)
                    })
//                    startForegroundService(LiveSyncService.getIntent(this, LiveSyncService.ACTION_SYNC_PAYMENT, it.payment.id))
//                    JobOrderPaymentSyncWorker.enqueue(this, it.payment.id)
                    JobOrderPaymentSyncService.start(this, it.payment.id)
                    viewModel.resetState()
                    finish()
                }
                is JobOrderPaymentViewModel.DataState.InvalidOperation -> {
                    showDialog(it.message)
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.InvalidInput -> {
                    fragment.show(supportFragmentManager, null)
                    viewModel.resetState()
                }
                is JobOrderPaymentViewModel.DataState.RequestModifyDateTime -> {
                    dateTimePicker.show(it.dateTime)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }
}