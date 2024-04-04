package com.vag.lmsapp.app.joborders.payment.delete

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.auth.AuthActionDialogActivity
import com.vag.lmsapp.databinding.ActivityJobOrderPaymentDeleteBinding
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.util.AuthLauncher
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.file
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPaymentDeleteActivity : AppCompatActivity() {
    private val viewModel: JobOrderPaymentDeleteViewModel by viewModels()
    private lateinit var binding: ActivityJobOrderPaymentDeleteBinding
    private val authLauncher = AuthLauncher(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_payment_delete)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        intent.getStringExtra(PAYMENT_ID).toUUID()?.let {
            viewModel.setPaymentId(it)
        }

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        authLauncher.onOk = { loginCredentials, code ->
            if(code == 1) {
                viewModel.confirm(loginCredentials.userId)
            }
        }
        binding.buttonConfirm.setOnClickListener {
            authLauncher.launch(listOf(EnumActionPermission.MODIFY_JOB_ORDER_PAYMENTS), 1)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    file(it.data).let {
                        if(it.exists()) {
                            it.delete()
                        }
                    }
                    setResult(RESULT_OK)
                    finish()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}