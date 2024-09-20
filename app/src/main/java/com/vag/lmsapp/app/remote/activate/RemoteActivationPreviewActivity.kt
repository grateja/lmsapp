package com.vag.lmsapp.app.remote.activate

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthResult
import com.vag.lmsapp.app.auth.AuthViewModel
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.app.printer.PrinterPreviewActivity
import com.vag.lmsapp.databinding.ActivityRemoteActivationPreviewBinding
import com.vag.lmsapp.model.MachineActivationQueues
import com.vag.lmsapp.model.MachineConnectionStatus
import com.vag.lmsapp.services.MachineActivationService
import com.vag.lmsapp.util.AuthLauncherActivity
import com.vag.lmsapp.util.Constants.Companion.CASCADE_CLOSE
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RemoteActivationPreviewActivity : AppCompatActivity() {
    companion object {
        private const val ACTION_ACTIVATE_MACHINE = "Activate machine"
    }

    private lateinit var binding: ActivityRemoteActivationPreviewBinding
    private val viewModel: RemoteActivationPreviewViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = AuthLauncherActivity(this).apply {
        onOk = { loginCredentials, code -> permitted(loginCredentials, code)}
    }

    private fun permitted(loginCredentials: LoginCredentials, code: String) {
        when(code) {
            ACTION_ACTIVATE_MACHINE -> {
                viewModel.prepareSubmit(loginCredentials.userId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_preview)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        subscribeEvents()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()

//        intent.getStringExtra(MachineActivationService.JO_SERVICE_ID_EXTRA).toUUID()?.let {
//            viewModel.setServiceId(it)
//        }
//        if(intent.action == MachineActivationService.CHECK_ONLY_EXTRA) {
//            intent.getStringExtra(Constants.MACHINE_ID_EXTRA).toUUID()?.let {
//                checkPending(it)
//            }
//        } else {
            intent.getParcelableExtra<MachineActivationQueues>(MachineActivationService.ACTIVATION_QUEUES_EXTRA)?.let {
                viewModel.setMachineActivationQueue(it)
                checkPending(it)
            }
//        }
//        intent.getStringExtra(MachineActivationService.CUSTOMER_ID_EXTRA).toUUID()?.let {
//            viewModel.setCustomerId(it)
//        }


        val filter = IntentFilter(MachineActivationService.MACHINE_ACTIVATION)
            .apply {
                addAction(MachineActivationService.MACHINE_ACTIVATION_READY)
                addAction(MachineActivationService.INPUT_INVALID_ACTION)
                addAction(MachineActivationService.DATABASE_INCONSISTENCIES_ACTION)
            }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    private fun subscribeObservers() {
        authViewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.Submit -> {
                    when(val authResult = it.data) {
                        is AuthResult.Authenticated -> {
                            permitted(authResult.loginCredentials, authResult.action)
                        }

                        is AuthResult.MandateAuthentication -> {
                            authLauncher.launch(authResult.permissions, authResult.action, true)
                        }

                        is AuthResult.OperationNotPermitted -> {
                            Snackbar.make(binding.root, authResult.message, Snackbar.LENGTH_LONG)
                                .setAction("Switch user") {
                                    authLauncher.launch(authResult.permissions, authResult.action, true)
                                }
                                .show()
                        }
                        // is AuthResult.NoAuthentication -> {
                        //     setupUi()
                        //  }
                    }
                    authViewModel.resetState()
                }
                else -> {}
            }
        })
        viewModel.machine.observe(this, Observer{
            title = "Activate ${it?.machineName()}"
        })
        viewModel.machineActivationQueue.observe(this, Observer {
            setFinishOnTouchOutside(it.status != MachineConnectionStatus.CONNECTING)
        })
        viewModel.dataState.observe(this, Observer {
            when(it) {
//                is RemoteActivationPreviewViewModel.DataState.CheckPending -> {
//                    checkPending(it.machineId)
//                    viewModel.resetState()
//                }
                is RemoteActivationPreviewViewModel.DataState.InitiateActivation -> {
                    val intent = Intent(applicationContext, MachineActivationService::class.java).apply {
                        putExtra(MachineActivationService.ACTIVATION_QUEUES_EXTRA, it.queue)
//                        putExtra(Constants.MACHINE_ID_EXTRA, it.machineId.toString())
//                        putExtra(MachineActivationService.JO_SERVICE_ID_EXTRA, it.workerId.toString())
//                        putExtra(MachineActivationService.CUSTOMER_ID_EXTRA, it.customerId.toString())
                    }
                    startForegroundService(intent)
                    viewModel.resetState()
                }
                is RemoteActivationPreviewViewModel.DataState.Dismiss -> {
                    setResult(it.result, Intent().apply {
                        putExtra("result", it.result)
                    })
                    finish()
                    viewModel.resetState()
                }
                is RemoteActivationPreviewViewModel.DataState.FixDataInconsistencies -> {
                    finish()
                    viewModel.resetState()
                }

                is RemoteActivationPreviewViewModel.DataState.InitiatePrint -> {
                    val intent = PrinterPreviewActivity.getIntent(this, it.machineUsage.printItems())
                    startActivity(intent)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {

        binding.buttonActivate.setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch {
                // Wait for 1 second
                delay(1000)
                // Re-enable the button
                it.isEnabled = true
            }
            authViewModel.authenticate(listOf(), ACTION_ACTIVATE_MACHINE, false)
//            authActivity.launch(listOf(), "Activate machine", false)
//            viewModel.prepareSubmit()
        }

        binding.buttonFix.setOnClickListener {
            viewModel.fix()
        }

        binding.buttonHide.setOnClickListener {
            setResult(RESULT_OK, Intent(CASCADE_CLOSE))
            finish()
        }

        binding.buttonPrint.setOnClickListener {
            viewModel.initiatePrint()
        }
    }

    private fun checkPending(queue: MachineActivationQueues) {
        val intent = Intent(this, MachineActivationService::class.java).apply {
            putExtra(MachineActivationService.CHECK_ONLY_EXTRA, true)
            putExtra(MachineActivationService.ACTIVATION_QUEUES_EXTRA, queue)
        }
        startForegroundService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // super.onBackPressed()
        viewModel.dismiss()
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            intent?.getParcelableExtra<MachineActivationQueues>(MachineActivationService.ACTIVATION_QUEUES_EXTRA)?.let {
                viewModel.updateQueue(it)
            }

            if(action == MachineActivationService.INPUT_INVALID_ACTION) {
                viewModel.setValidationMessage(intent.getStringExtra(MachineActivationService.MESSAGE_EXTRA))
            }

//            if(action == MachineActivationService.MACHINE_ACTIVATION_READY) {
//                binding.buttonActivate.visibility = View.VISIBLE
//            } else if(action == MachineActivationService.MACHINE_ACTIVATION) {
//                intent.getParcelableExtra<MachineActivationQueues>(MachineActivationService.ACTIVATION_QUEUES_EXTRA).let {
//                    if(it != null) {
////                        viewModel.setMachineActivationQueue(it)
////                        viewModel.setMachineStatus(it.status)
////                        viewModel.setServiceId(it.jobOrderServiceId)
////                        viewModel.setCustomerId(it.customerId)
////                        viewModel.setMessage(it.message)
////                        if(it.connecting() || it.status == MachineConnectionStatus.SUCCESS) {
////                            binding.buttonActivate.visibility = View.GONE
////                        } else {
////                            binding.buttonActivate.visibility = View.VISIBLE
////                        }
////                    } else {
////                        binding.buttonActivate.visibility = View.VISIBLE
//                    }
//                }
//            } else if(action == MachineActivationService.DATABASE_INCONSISTENCIES_ACTION) {
////                binding.buttonActivate.visibility = View.GONE
//                println("inconsistencies")
//                viewModel.setValidationMessage(intent.getStringExtra(MachineActivationService.MESSAGE_EXTRA))
//                binding.buttonFix.visibility = View.VISIBLE
//            }
        }
    }
}