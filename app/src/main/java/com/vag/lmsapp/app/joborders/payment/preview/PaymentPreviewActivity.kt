package com.vag.lmsapp.app.joborders.payment.preview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.app.joborders.payment.JobOrderListPaymentAdapter
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentActivity.Companion.PAYMENT_ID
import com.vag.lmsapp.databinding.ActivityPaymentPreviewBinding
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PaymentPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentPreviewBinding
    private val viewModel: PaymentPreviewViewModel by viewModels()
    private val adapter = JobOrderListPaymentAdapter(true) //Adapter<JobOrderPaymentMinimal>(R.layout.recycler_item_job_order_read_only)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerJobOrderPaymentMinimal.adapter = adapter

        intent.getStringExtra(PAYMENT_ID)?.toUUID()?.let {
            viewModel.setPaymentId(it)
        }

        subscribeListeners()
        subscribeEvents()

        window.statusBarColor = resources.getColor(R.color.color_code_payments, null)
    }

    private fun openJobOrder(jobOrderId: UUID) {
        val intent = Intent(this, JobOrderCreateActivity::class.java).apply {
            putExtra(JobOrderCreateActivity.JOB_ORDER_ID, jobOrderId.toString())
            action = JobOrderCreateActivity.ACTION_LOAD_BY_JOB_ORDER_ID
        }
        startActivity(intent)
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            openJobOrder(it.id)
        }
    }

    private fun subscribeListeners() {
        viewModel.jobOrders.observe(this, Observer {
            adapter.setData(it)
        })
    }
}