package com.vag.lmsapp.app.remote.print

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityRemoteActivationPrintBinding
import com.vag.lmsapp.services.PrinterService
import com.vag.lmsapp.services.PrinterService.Companion.PAYLOAD_TEXT
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationPrintActivity : AppCompatActivity() {
    companion object {
        const val MACHINE_USAGE_ID = "machine_usage_id"
    }
    private lateinit var binding: ActivityRemoteActivationPrintBinding
    private val viewModel: RemoteActivationPrintViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_activation_print)

        intent.getStringExtra(MACHINE_USAGE_ID).toUUID()?.let {
            viewModel.setMachineUsageId(it)
        }
    }

    private fun startPrint(payload: String) {
        val intent = Intent(this, PrinterService::class.java).apply {
            putExtra(PAYLOAD_TEXT, payload)
        }
        startForegroundService(intent)
    }
}