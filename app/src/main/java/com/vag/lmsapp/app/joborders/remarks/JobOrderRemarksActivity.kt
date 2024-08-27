package com.vag.lmsapp.app.joborders.remarks

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.cancel.JobOrderCancelActivity
import com.vag.lmsapp.databinding.ActivityJobOrderRemarksBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.services.JobOrderSyncService
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderRemarksActivity : AppCompatActivity() {
    private val viewModel: JobOrderRemarksViewModel by viewModels()
    private lateinit var binding: ActivityJobOrderRemarksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_remarks)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.cardButtonConfirm.setOnClickListener {
            viewModel.save()
        }
        binding.cardButtonClose.setOnClickListener {
            finish()
        }
        intent.getStringExtra(Constants.JOB_ORDER_ID).toUUID()?.let {
            viewModel.loadJobOrder(it)
        }
        subscribeListeners()
    }
    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    finish()
                }

                is DataState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}