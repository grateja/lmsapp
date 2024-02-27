package com.vag.lmsapp.app.remote.queues

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.remote.activate.RemoteActivationPreviewActivity
import com.vag.lmsapp.databinding.ActivityRemoteActivationQueuesBinding
import com.vag.lmsapp.room.entities.EntityAvailableService
import com.vag.lmsapp.room.entities.EntityCustomerQueueService
import com.vag.lmsapp.services.MachineActivationService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.CASCADE_CLOSE
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationQueuesActivity : AppCompatActivity() {
    companion object {
        const val CUSTOMER_QUEUE_EXTRA = "customerQueue"
    }
    private lateinit var binding: ActivityRemoteActivationQueuesBinding
    private val viewModel: RemoteQueuesViewModel by viewModels()
    private val serviceQueuesAdapter = Adapter<EntityAvailableService>(R.layout.recycler_item_queue_service)
    private val launcher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_queues)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerAvailableServices.adapter = serviceQueuesAdapter

        subscribeListeners()
        subscribeEvents()
        intent.getParcelableExtra<EntityCustomerQueueService>(CUSTOMER_QUEUE_EXTRA).let {
            viewModel.setCustomerQueue(it)
        }

        intent.getStringExtra(Constants.MACHINE_ID_EXTRA).toUUID()?.let {
            viewModel.setMachineId(it)
        }

        window.statusBarColor = resources.getColor(R.color.color_code_machines, null)
    }

    private fun subscribeEvents() {
        serviceQueuesAdapter.onItemClick = {
            it.id?.let { joServiceId ->
                viewModel.openActivationPreview(joServiceId)
            }
        }

        launcher.onOk = {
            if(it.data?.action == CASCADE_CLOSE) {
                setResult(RESULT_OK, Intent(CASCADE_CLOSE))
                finish()
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.machine.observe(this, Observer {
            if(it?.activationRef?.running() == true) {
                finish()
            }
        })
        viewModel.availableServices.observe(this, Observer {
            it?.let {
                serviceQueuesAdapter.setData(it)
            }
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is RemoteQueuesViewModel.NavigationState.OpenActivationPreview -> {
                    val intent = Intent(this, RemoteActivationPreviewActivity::class.java).apply {
//                        putExtra(MachineActivationService.JO_SERVICE_ID_EXTRA, it.joServiceId.toString())
//                        putExtra(MachineActivationService.CUSTOMER_ID_EXTRA, it.customerId.toString())
//                        putExtra(Constants.MACHINE_ID_EXTRA, it.machineId.toString())
                        putExtra(MachineActivationService.ACTIVATION_QUEUES_EXTRA, it.queue)
                    }
                    launcher.launch(intent)
                    viewModel.resetNavigationState()
                }

                else -> {}
            }
        })
    }
}