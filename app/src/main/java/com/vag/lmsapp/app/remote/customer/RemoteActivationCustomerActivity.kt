package com.vag.lmsapp.app.remote.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.remote.queues.RemoteActivationQueuesActivity
import com.vag.lmsapp.databinding.ActivityRemoteActivationSelectCustomerBinding
import com.vag.lmsapp.room.entities.EntityCustomerQueueService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoteActivationSelectCustomerBinding
    private val viewModel: RemoteCustomerViewModel by viewModels()
    private val customerQueuesAdapter = Adapter<EntityCustomerQueueService>(R.layout.recycler_item_queue_customer)
    private val launcher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_select_customer)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerCustomerQueues.adapter = customerQueuesAdapter

        subscribeListeners()
        subscribeEvents()
        intent.getStringExtra(Constants.MACHINE_ID).toUUID()?.let {
            viewModel.setMachineId(it)
        }

        window.statusBarColor = resources.getColor(R.color.color_code_machines, null)
    }

    private fun subscribeEvents() {
        customerQueuesAdapter.onItemClick = {
            viewModel.openQueueServices(it)
        }

        launcher.onOk = {
            if(it.data?.action == Constants.CASCADE_CLOSE) {
                setResult(RESULT_OK, Intent(Constants.CASCADE_CLOSE))
                finish()
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.machine.observe(this, Observer {
//            title = "Select customer for  ${it?.machineName()}"
            if(it?.activationRef?.running() == true) {
                finish()
            }
        })

        viewModel.customerQueues.observe(this, Observer {
            it?.let {
                customerQueuesAdapter.setData(it)
            }
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is RemoteCustomerViewModel.NavigationState.OpenQueuesServices -> {
                    val intent = Intent(this, RemoteActivationQueuesActivity::class.java).apply {
                        putExtra(RemoteActivationQueuesActivity.CUSTOMER_QUEUE_EXTRA, it.customerQueueService)
                        putExtra(Constants.MACHINE_ID, it.machineId.toString())
                    }
                    launcher.launch(intent)
                    viewModel.resetNavigationState()
                }

                else -> {}
            }
        })
    }
}