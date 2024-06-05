package com.vag.lmsapp.app.remote.running

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.remote.end_time.RemoteActivationEndTimeActivity
import com.vag.lmsapp.databinding.ActivityRemoteActivationRunningBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationRunningActivity : AppCompatActivity() {

    private val viewModel: RemoteRunningViewModel by viewModels()
    private lateinit var binding: ActivityRemoteActivationRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_running)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeListeners()
        subscribeEvents()

        intent.getStringExtra(Constants.MACHINE_ID_EXTRA).toUUID().let {
            viewModel.get(it)
        }
    }

    private fun subscribeEvents() {
        binding.buttonEndTime.setOnClickListener {
            viewModel.initiateEntTime()
        }
    }

    private fun subscribeListeners() {
        viewModel.runningMachine.observe(this, Observer {
            title = "${it?.machine?.machineName()} running"
        })
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is RemoteRunningViewModel.NavigationState.InitiateEndTime -> {
                    val intent = Intent(this, RemoteActivationEndTimeActivity::class.java).apply {
                        putExtra(ID, it.machineId.toString())
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }
}