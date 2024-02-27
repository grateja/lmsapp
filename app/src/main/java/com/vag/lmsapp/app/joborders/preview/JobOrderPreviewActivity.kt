package com.vag.lmsapp.app.joborders.preview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity
import com.vag.lmsapp.databinding.ActivityJobOrderPreviewBinding
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderPreviewBinding
//    private val printerViewModel: BluetoothPrinterViewModel by viewModels()

    companion object {
        const val JOB_ORDER_ID = "jobOrderId"
    }

    private val viewModel: JobOrderPreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_preview)

        subscribeEvents()
        subscribeListeners()

        viewModel.getByJobOrderId(
            intent.getStringExtra("jobOrderId").toUUID()
        )

//        intent.getStringExtra("jobOrderId")?.let {
//            try {
//                viewModel.setJobOrderId(UUID.fromString(it))
//            } catch (e: Exception) {
//                finish()
//            }
//        }

//        intent.getStringExtra("customerId")?.let {
//            try {
//                viewModel.setCustomerId(UUID.fromString(it))
//            } catch (e: Exception) {
//                finish()
//            }
//        }

//        printerViewModel.initializeBluetooth(this)
//        printerViewModel.loadPrinters()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, androidx.lifecycle.Observer {
            when(it) {
                is JobOrderPreviewViewModel.DataState.OpenPayment -> {
                    val intent = Intent(applicationContext, JobOrderPaymentActivity::class.java).apply {
                        putExtra("customerId", it.customerId.toString())
                    }
                    startActivity(intent)
                }
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonFinish.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        binding.buttonLabel.setOnClickListener {
            printLabel()
        }
    }

    private fun printLabel() {
//        printerViewModel.loadPrinters()
//        printerViewModel.selectPrinter()
    }
}