package com.vag.lmsapp.app.joborders.cancel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.databinding.ActivityJobOrderCancelBinding
import com.vag.lmsapp.internet.InternetConnectionCallback
import com.vag.lmsapp.internet.InternetConnectionObserver
import com.vag.lmsapp.services.JobOrderSyncService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderCancelActivity : AppCompatActivity(), InternetConnectionCallback {

    companion object {
//        const val JOB_ORDER_ID = "jobOrderId"
        const val ACTION_DELETE_JOB_ORDER = "deleteJobOrder"
    }

    private lateinit var binding: ActivityJobOrderCancelBinding
    private val viewModel: JobOrderCancelViewModel by viewModels()
    private val authLauncher = ActivityLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_cancel)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.buttonSave.setOnClickListener {
            viewModel.validate()
        }
        authLauncher.onOk = { result ->
            result.data?.getParcelableExtra<LoginCredentials>(AuthActionDialogActivity.RESULT)?.let {
                viewModel.save(it.userId)
            }
        }
        intent.getStringExtra(JOB_ORDER_ID).toUUID().let {
            viewModel.loadJobOrder(it)
        }
        subscribeListeners()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.ValidationPassed -> {
                    val intent = Intent(this, AuthActionDialogActivity::class.java)
                    authLauncher.launch(intent)
                    viewModel.resetState()
                }
                is DataState.SaveSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        action = ACTION_DELETE_JOB_ORDER
                        putExtra(JOB_ORDER_ID, it.data.toString())
                    })
                    finish()
                    if(internetAvailable) {
                        JobOrderSyncService.start(this, it.data)
                    }
                }
                is DataState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    var internetAvailable = false

    override fun onPause() {
        super.onPause()
        InternetConnectionObserver.unRegister()
    }

    override fun onConnected() {
        internetAvailable = true
        println("internet available")
    }

    override fun onDisconnected() {
        internetAvailable = false
        println("no internet available")
    }
    override fun onResume() {
        super.onResume()

        InternetConnectionObserver
            .instance(this)
            .setCallback(this)
            .register()
    }
}