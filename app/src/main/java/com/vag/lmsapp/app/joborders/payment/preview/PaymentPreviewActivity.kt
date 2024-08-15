package com.vag.lmsapp.app.joborders.payment.preview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.payment.JobOrderListPaymentAdapter
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentViewModel
import com.vag.lmsapp.app.joborders.payment.delete.JobOrderPaymentDeleteActivity
import com.vag.lmsapp.app.joborders.preview.JobOrderPreviewBottomSheetFragment
import com.vag.lmsapp.databinding.ActivityPaymentPreviewBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.CUSTOMER_ID
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.showConfirmationDialog
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PaymentPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentPreviewBinding
    private val viewModel: JobOrderPaymentViewModel by viewModels()
    private val adapter = JobOrderListPaymentAdapter(true) //Adapter<JobOrderPaymentMinimal>(R.layout.recycler_item_job_order_read_only)
    private val launcher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_preview)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
//        setSupportActionBar(binding.toolbar)

        binding.recyclerJobOrderPaymentMinimal.adapter = adapter

        val customerId = intent.getStringExtra(CUSTOMER_ID)?.toUUID()
        val paymentId = intent.getStringExtra(PAYMENT_ID)?.toUUID()

        viewModel.getPayment(paymentId, customerId)

        subscribeListeners()
        subscribeEvents()

        window.statusBarColor = resources.getColor(R.color.white, null)
    }

    private fun openJobOrder(jobOrderId: UUID) {
        JobOrderPreviewBottomSheetFragment.newInstance(true, jobOrderId).show(supportFragmentManager, null)
    }

    private fun initiateDelete(paymentId: UUID) {
        showConfirmationDialog("Delete payment?", "Are you sure you want to delete this payment? All the job orders associated to this payment will be marked as unpaid") {
            val intent = Intent(this, JobOrderPaymentDeleteActivity::class.java).apply {
                putExtra(PAYMENT_ID, paymentId.toString())
            }
            launcher.launch(intent)
        }
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            openJobOrder(it.id)
        }
        launcher.onOk = {
            finish()
        }
        binding.buttonDelete.setOnClickListener {
            viewModel.initiateDelete()
        }
    }

    private fun subscribeListeners() {
        viewModel.jobOrders.observe(this, Observer {
            adapter.setData(it)
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is JobOrderPaymentViewModel.DataState.InitiateDelete -> {
                    initiateDelete(it.paymentId)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_delete_menu, menu)
        menu?.findItem(R.id.menu_save)?.isVisible = false
        menu?.findItem(R.id.menu_delete)?.setOnMenuItemClickListener {
            viewModel.initiateDelete()
            true
        }
        return super.onCreateOptionsMenu(menu)
    }
}