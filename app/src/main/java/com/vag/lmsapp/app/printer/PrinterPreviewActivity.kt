package com.vag.lmsapp.app.printer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.app.joborders.print.PrinterAdapter
import com.vag.lmsapp.databinding.ActivityPrinterPreviewBinding
import com.vag.lmsapp.model.EnumPrintState
import com.vag.lmsapp.model.PrinterItem
import com.vag.lmsapp.services.PrinterService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.BluetoothPrinterHelper
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.showDialog
import com.vag.lmsapp.util.spToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrinterPreviewActivity : AppCompatActivity() {
    companion object {
        const val PRINTER_ITEMS_EXTRA = "printer_items_extra"

        fun getIntent(context: Context, items: List<PrinterItem>): Intent {
            return Intent(context, PrinterPreviewActivity::class.java).apply {
                putParcelableArrayListExtra(PRINTER_ITEMS_EXTRA, ArrayList(items))
            }
        }
    }

    private val viewModel: PrinterPreviewViewModel by viewModels()
    private lateinit var binding: ActivityPrinterPreviewBinding
    private val adapter = PrinterAdapter()
    private val helper = BluetoothPrinterHelper(this)
    private val launcher = ActivityLauncher(this)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                PrinterService.PRINT_ACTION -> handlePrintAction(intent)
            }
        }
    }

    private fun getWidth(charactersPerLine: Int): Int {
        val characterWidthInDp = 18f
        val paint = Paint().apply {
            textSize = characterWidthInDp.spToPx()
            typeface = Typeface.MONOSPACE
        }
        return paint.measureText("w".repeat(charactersPerLine)).toInt()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_options) {
            openPrinterSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        helper.registerReceiver(this)
        val filter = IntentFilter().apply {
            addAction(PrinterService.PRINT_ACTION)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
        viewModel.setPermissionStatus(helper.permitted())
    }

    override fun onPause() {
        super.onPause()
        helper.unregisterReceiver()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun openPrinterSettings() {
        val intent = Intent(this, SettingsPrinterActivity::class.java)
        startActivity(intent)
    }

    private fun handlePrintAction(intent: Intent) {
        intent.getParcelableExtra<EnumPrintState>(PrinterService.PRINT_STATE)?.let { printState ->
            viewModel.setPrintState(printState)

            if (printState == EnumPrintState.ERROR) {
                intent.getStringExtra(PrinterService.MESSAGE)?.let { errorMessage ->
                    showErrorMessage(errorMessage)
                }
            }
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        showDialog(errorMessage)
    }

    private fun cancel() {
        val intent = Intent(this, PrinterService::class.java).apply {
            action = PrinterService.CANCEL_PRINT_ACTION
        }
        startForegroundService(intent)
    }

    private fun print(formattedText: String) {
        val intent = Intent(this, PrinterService::class.java).apply {
            putExtra(PrinterService.PAYLOAD_TEXT, formattedText)
        }
        startForegroundService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_printer_preview)
        setSupportActionBar(binding.toolbar)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerItems.adapter = adapter

        intent.getParcelableArrayListExtra<PrinterItem>(PRINTER_ITEMS_EXTRA)?.toList()?.let {
            viewModel.setPrinterItems(it)
        }

        subscribeEvents()
        subscribeListeners()
    }
    private fun subscribeEvents() {
        helper.setOnBluetoothStateChanged {
            viewModel.setBluetoothState(it)
        }
        launcher.onOk = {
            it.data?.getParcelableExtra<PrinterDevice>(SettingsPrinterActivity.PRINTER_DEVICE_EXTRA)?.let(viewModel::setDevice)
        }

        binding.buttonPrint.setOnClickListener {
            viewModel.print()
        }
        binding.buttonEnableBluetooth.setOnClickListener {
            helper.enableBluetooth()
        }
        binding.textPrinterName.setOnClickListener {
            openPrinterSettings()
        }
    }
    private fun subscribeListeners() {
        viewModel.characterLength.observe(this, Observer {
            binding.wrapper.layoutParams.width = getWidth(it) + 32
        })

        viewModel.printerItems.observe(this, Observer {
            adapter.setData(it)
        })
        viewModel.dataState.observe(this, Observer {
            when (it) {
                is DataState.Submit -> {
                    print(it.data)
                    viewModel.resetState()
                }
                is DataState.Invalidate -> {
                    cancel()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }
}