package com.vag.lmsapp.app.joborders.create.delivery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.JobOrderCreateActivity
import com.vag.lmsapp.databinding.ActivityJobOrderCreateSelectDeliveryBinding
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCreateSelectDeliveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobOrderCreateSelectDeliveryBinding
    private val viewModel: DeliveryViewModel by viewModels()
    private val deliveryVehiclesAdapter = DeliveryVehiclesAdapter()
    private lateinit var deliveryProfileModal: CreateJobOrderSelectDeliveryBottomSheetFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_create_select_delivery)
        binding.lifecycleOwner = this
        binding.recyclerDeliveryVehicles.adapter = deliveryVehiclesAdapter
        binding.viewModel = viewModel

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        action = intent.action
                        putExtra(JobOrderCreateActivity.PAYLOAD_EXTRA, it.data)
                    })
                    viewModel.resetState()
                    finish()
                }
                else -> {}
            }
        })

        viewModel.deliveryProfiles.observe(this, Observer {
            deliveryVehiclesAdapter.setData(it)
            intent.getParcelableExtra<DeliveryCharge>(JobOrderCreateActivity.PAYLOAD_EXTRA)?.let { deliveryCharge ->
                if(!deliveryCharge.deleted) {
                    viewModel.setDeliveryCharge(deliveryCharge)
                }
            }
        })

        viewModel.profile.observe(this, Observer {
            deliveryVehiclesAdapter.notifySelection(it.deliveryProfileRefId)
        })
    }

    private fun subscribeEvents() {
        deliveryVehiclesAdapter.onItemClick = {
            viewModel.setDeliveryProfile(it.deliveryProfileRefId)
            showOptions()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.buttonRemove.setOnClickListener {
            viewModel.prepareDeliveryCharge(true)
        }
    }

    private fun showOptions() {
        deliveryProfileModal = CreateJobOrderSelectDeliveryBottomSheetFragment.newInstance().apply {
            onOk = {
                viewModel.prepareDeliveryCharge(false)
            }
        }
        deliveryProfileModal.show(supportFragmentManager, this.toString())
    }
}