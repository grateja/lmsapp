package com.vag.lmsapp.app.machines.ping

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
import com.vag.lmsapp.databinding.ActivityPingBinding
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PingActivity : AppCompatActivity() {
    companion object {
        const val IP_END_EXTRA = "ip_end"
    }
    private val viewModel: PingViewModel by viewModels()
    private lateinit var binding: ActivityPingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ping)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()

        intent.getIntExtra(IP_END_EXTRA, 0).let {
            viewModel.start(it)
        }
    }

    private fun subscribeEvents() {
        binding.buttonCancel.setOnClickListener {
            viewModel.cancel()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancel()
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when(it) {
                is DataState.SaveSuccess -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                    finish()
                }

                is DataState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                    finish()
                }

                else -> {}
            }
        })
    }
}