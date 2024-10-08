package com.vag.lmsapp.app.joborders.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityJobOrderPaymentBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumPaymentMethod
import com.vag.lmsapp.services.JobOrderPaymentSyncService
import com.vag.lmsapp.util.*
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPaymentActivity : AppCompatActivity() {

    companion object {
        const val SELECTED_JOB_ORDER_IDS = "jobOrderIds"
        const val AUTH_REQUEST_MODIFY_DATE_ACTION = "modifyDate"
        const val AUTH_REQUEST_SAVE = "save"
        const val ACTION_LOAD_BY_CUSTOMER = "loadByCustomer"
        const val ACTION_LOAD_BY_PAYMENT = "loadByPayment"
    }

    private lateinit var binding: ActivityJobOrderPaymentBinding
    private val viewModel: JobOrderPaymentViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val fragment = BottomSheetJobOrderPaymentFragment()
    private val adapter = JobOrderListPaymentAdapter(false)

    private val dateTimePicker: DateTimePicker by lazy {
        DateTimePicker(this)
    }

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            AUTH_REQUEST_MODIFY_DATE_ACTION -> {
                viewModel.requestModifyDateTime()
            }

            AUTH_REQUEST_SAVE -> {
                viewModel.save(loginCredentials.userId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_payment)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerJobOrderPaymentMinimal.adapter = adapter

        window.statusBarColor = resources.getColor(R.color.white, null)

        subscribeEvents()
        subscribeListeners()

        if(intent.action == ACTION_LOAD_BY_CUSTOMER) {
            intent.getStringExtra(CUSTOMER_ID).toUUID()?.let {
                viewModel.getUnpaidByCustomerId(it)
            }
        } else if(intent.action == ACTION_LOAD_BY_PAYMENT) {
            val paymentId = intent.getStringExtra(PAYMENT_ID).toUUID()
            val customerId = intent.getStringExtra(CUSTOMER_ID).toUUID()
            viewModel.getPayment(paymentId, customerId)
        }
    }

//    private fun auth(action: String, message: String) {
//        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//            this.action = action
//            putExtra(AuthActionDialogActivity.LAUNCH_CODE, message)
//        }
//        authLauncher.launch(intent)
//    }

    private fun subscribeEvents() {
        binding.cardPaymentOptionCash.setOnClickListener {
            viewModel.setPaymentMethod(EnumPaymentMethod.CASH)
            fragment.show(supportFragmentManager, null)
        }
        binding.cardPaymentOptionCashless.setOnClickListener {
            viewModel.setPaymentMethod(EnumPaymentMethod.CASHLESS)
            fragment.show(supportFragmentManager, null)
        }
        binding.cardPaymentOptionMixed.setOnClickListener {
            viewModel.setPaymentMethod(EnumPaymentMethod.MIXED)
            fragment.show(supportFragmentManager, null)
        }
        binding.cardDatePaid.setOnClickListener {
            authViewModel.authenticate(listOf(EnumActionPermission.MODIFY_JOB_ORDER_PAYMENTS), AUTH_REQUEST_MODIFY_DATE_ACTION, false)
//            auth(AUTH_REQUEST_MODIFY_DATE_ACTION, "Modification of date paid requires authentication!")
        }
        adapter.onSelectionChange = {
            viewModel.selectItem(it)
        }
        adapter.onItemClick = {
            JobOrderPreviewBottomSheetFragment.newInstance(true, it.id).show(supportFragmentManager, null)
        }
        binding.cardButtonConfirm.setOnClickListener {
            viewModel.validate()
        }
        dateTimePicker.setOnDateTimeSelectedListener {
            viewModel.setDateTime(it)
            Toast.makeText(this, "Date time paid modified", Toast.LENGTH_SHORT).show()
        }
//        authLauncher.onOk = { result ->
//            when(result.data?.action) {
//                AUTH_REQUEST_SAVE -> {
//                    result.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
//                        viewModel.save(it.userId)
//                    }
//                }
//                AUTH_REQUEST_MODIFY_DATE_ACTION -> {
//                    viewModel.requestModifyDateTime()
//                }
//            }
//        }
    }

    private fun subscribeListeners() {
        authViewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.Submit -> {
                    when(val authResult = it.data) {
                        is AuthResult.Authenticated -> {
                            permitted(authResult.loginCredentials, authResult.action)
                        }

                        is AuthResult.MandateAuthentication -> {
                            authLauncher.launch(authResult.permissions, authResult.action, true)
                        }

                        is AuthResult.OperationNotPermitted -> {
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG)
                                .setAnchorView(binding.controls)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                        // is AuthResult.NoAuthentication -> {
                        //     setupUi()
                        //  }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })

        viewModel.customer.observe(this, Observer {
            title = "${it.name} - [${it.crn}]"
        })
        viewModel.payableJobOrders.observe(this, Observer {
            adapter.setData(it)
        })
//        viewModel.amountToPay.observe(this, Observer {
//        })

        viewModel.dataState.observe(this, Observer {
            when(it) {
                is JobOrderPaymentViewModel.DataState.ValidationPassed -> {
                    authViewModel.authenticate(listOf(), AUTH_REQUEST_SAVE, false)
//                    auth(AUTH_REQUEST_SAVE, "Confirm and save payment")
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