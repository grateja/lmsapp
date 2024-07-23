package com.vag.lmsapp.app.joborders.create.discount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectDiscountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectDiscountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderCreateSelectDiscountBinding
    private val viewModel: DiscountViewModel by viewModels()
    private val adapter = DiscountsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_discount)
        binding.lifecycleOwner = this
        binding.recyclerDiscounts.adapter = adapter
        binding.viewModel = viewModel

        subscribeEvents()
        subscribeListeners()

        viewModel.loadDiscounts()
    }

    private fun subscribeListeners() {
        viewModel.discounts.observe(this, Observer {
            adapter.setData(it)
            intent.getParcelableExtra<MenuDiscount>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.let { discount ->
                if(!discount.deleted) {
                    viewModel.setDiscount(discount)
                }
            }
        })
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            setResult(RESULT_OK, Intent().apply {
                action = intent.action
                putExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, it)
            })
            finish()
        }

        binding.cardButtonClose.setOnClickListener {
            finish()
        }

        binding.cardButtonDelete.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                action = intent.action
                putExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, "")
            })
            finish()
        }
    }
}