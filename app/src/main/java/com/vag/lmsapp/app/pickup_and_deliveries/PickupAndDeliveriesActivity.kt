package com.vag.lmsapp.app.pickup_and_deliveries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.joborders.create.delivery.DeliveryVehiclesAdapter
import com.vag.lmsapp.app.pickup_and_deliveries.add_edit.AddEditDeliveryProfileActivity
import com.vag.lmsapp.databinding.ActivityPickupAndDeliveriesBinding
import com.vag.lmsapp.util.CrudActivity
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class PickupAndDeliveriesActivity : AppCompatActivity() {
    private val viewModel: PickupAndDeliveriesViewModel by viewModels()
    private lateinit var binding: ActivityPickupAndDeliveriesBinding
    private val adapter = DeliveryVehiclesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pickup_and_deliveries)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerDeliveryProfiles.adapter = adapter

        subscribeListeners()
        subscribeEvents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.filter(true)
    }

    private fun openAddEdit(id: UUID) {
        val intent = Intent(this, AddEditDeliveryProfileActivity::class.java).apply {
            putExtra(CrudActivity.ENTITY_ID, id.toString())
        }
        startActivity(intent)
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            openAddEdit(it.deliveryProfileRefId)
        }
    }

    private fun subscribeListeners() {
        viewModel.filterState.observe(this, Observer {
            when(it) {
                is FilterState.LoadItems -> {
                    if(it.reset) {
                        adapter.setData(it.items)
                    } else {
//                        adapter.addItems(it.items)
                    }
                    viewModel.clearState()
                }

                else -> {}
            }
        })
    }
}