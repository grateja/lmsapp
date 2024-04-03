package com.vag.lmsapp.app.app_settings.printer

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.app.app_settings.printer.browser.AppSettingsPrinterBrowserActivity
import com.vag.lmsapp.databinding.ActivityAppSettingsPrinterBinding
import com.vag.lmsapp.model.EnumPrintState
import com.vag.lmsapp.services.PrinterService
import com.vag.lmsapp.services.PrinterService.Companion.CANCEL_PRINT_ACTION
import com.vag.lmsapp.services.PrinterService.Companion.PAYLOAD_TEXT
import com.vag.lmsapp.services.PrinterService.Companion.PRINT_ACTION
import com.vag.lmsapp.services.PrinterService.Companion.PRINT_STATE
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.showTextInputDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsPrinterActivity : AppCompatActivity() {
    companion object {
        const val ACTION_BROWSE_PRINTER_DEVICES = "browse_printer_devices"
        const val PRINTER_DEVICE_EXTRA = "printer_device"
    }

    private val viewModel: PrinterSettingsViewModel by viewModels()

    private lateinit var binding: ActivityAppSettingsPrinterBinding

    private val launcher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_printer)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeEvents()
        subscribeListeners()
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getParcelableExtra<EnumPrintState>(PRINT_STATE)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(PRINT_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPrint()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun subscribeEvents() {
        binding.cardPrinter.card.setOnClickListener {
            viewModel.openPrinterBrowser()
        }
        binding.cardWidth.card.setOnClickListener {
            viewModel.openPrinterWidth()
        }
        binding.cardCharactersPerLine.card.setOnClickListener {
            viewModel.openPrinterCharactersPerLine()
        }
        launcher.onOk = {
            when(it.data?.action) {
                ACTION_BROWSE_PRINTER_DEVICES -> {
                    it.data?.getParcelableExtra<PrinterDevice>(PRINTER_DEVICE_EXTRA)?.let { printerDevice ->
                        viewModel.setPrinterDevice(printerDevice)
                    }
                }
            }
        }
        binding.switchJobOrderItemized.setOnCheckedChangeListener { _, checked ->
            viewModel.updateJobOrderItemized(checked)
        }
        binding.switchJobOrderShowItemPrice.setOnCheckedChangeListener { _, checked ->
            viewModel.updateJobOrderShowItemPrice(checked)
        }
        binding.switchClaimStubItemized.setOnCheckedChangeListener { _, checked ->
            viewModel.updateClaimStubItemized(checked)
        }
        binding.switchClaimStubShowItemPrice.setOnCheckedChangeListener { _, checked ->
            viewModel.updateClaimStubShowItemPrice(checked)
        }
        binding.cardDisclaimer.setOnClickListener {
            viewModel.openDisclaimer()
        }
        binding.switchShowDisclaimer.setOnCheckedChangeListener { _, checked ->
            viewModel.updateShowDisclaimer(checked)
        }
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when (it) {
                is PrinterSettingsViewModel.DataState.OpenPrinterBrowser -> {
                    val intent = Intent(this, AppSettingsPrinterBrowserActivity::class.java).apply {
                        action = ACTION_BROWSE_PRINTER_DEVICES
                        putExtra(PRINTER_DEVICE_EXTRA, it.currentPrinter)
                    }
                    launcher.launch(intent)
                    viewModel.resetState()
                }
                is PrinterSettingsViewModel.DataState.OpenPrinterWidth -> {
                    showTextInputDialog("Paper width in mm", null, it.width) { result ->
                        result?.let {
                            viewModel.setPrinterWidth(it)
                        }
                    }
                    viewModel.resetState()
                }
                is PrinterSettingsViewModel.DataState.OpenPrinterCharactersPerLine -> {
                    showTextInputDialog("Maximum characters per line", null, it.charactersPerLine) { result ->
                        result?.let {
                            viewModel.setPrinterCharactersPerLine(it)
                        }
                    }
                    viewModel.resetState()
                    viewModel.resetState()
                }
                is PrinterSettingsViewModel.DataState.OpenDisclaimer -> {
                    showTextInputDialog("Disclaimer", null, it.text) { result ->
                        viewModel.updateDisclaimer(result)
                    }
                    viewModel.resetState()
                }
                is PrinterSettingsViewModel.DataState.Save -> {
                    viewModel.resetState()
                    finish()
                }
                is PrinterSettingsViewModel.DataState.StartTestPrint -> {
                    val intent = Intent(this, PrinterService::class.java).apply {
                        putExtra(PAYLOAD_TEXT, it.payload)
                    }
                    println("start test print")
                    startForegroundService(intent)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun cancelPrint() {
        val intent = Intent(this, PrinterService::class.java).apply {
            action = CANCEL_PRINT_ACTION
        }
        startForegroundService(intent)
    }
}