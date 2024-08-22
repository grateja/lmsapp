package com.vag.lmsapp.app.joborders.print

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.OptionsAdapter
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity.Companion.PRINTER_DEVICE_EXTRA
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.databinding.ActivityJobOrderPrintBinding
import com.vag.lmsapp.model.EnumPrintState
//import com.csi.palabakosys.model.PrintItem
import com.vag.lmsapp.services.PrinterService
import com.vag.lmsapp.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobOrderPrintActivity : AppCompatActivity() {

    companion object {
        const val TAB_CLAIM_STUB = "Claim Stub"
        const val TAB_JOB_ORDER = "Job Order"
        const val TAB_MACHINE_STUB = "Machine Activation Stub"
    }

    private val viewModel: JobOrderPrintViewModel by viewModels()
    private lateinit var binding: ActivityJobOrderPrintBinding
    private val helper = BluetoothPrinterHelper(this)
    private val itemsAdapter = PrinterAdapter()
    private val launcher = ActivityLauncher(this)
    private val printOptions = OptionsAdapter(
        R.layout.recycler_item_print_option,
        arrayOf(TAB_CLAIM_STUB, TAB_JOB_ORDER, TAB_MACHINE_STUB)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_order_print)

        setSupportActionBar(binding.toolbar)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerItems.adapter = itemsAdapter
        binding.printOptions.adapter = printOptions
        binding.printOptions.layoutManager = GridLayoutManager(applicationContext, 3)

        subscribeEvents()
        subscribeListeners()

        intent.getStringExtra(Constants.ID)?.toUUID()?.let(viewModel::setJobOrderId)
    }

    private fun getWidth(charactersPerLine: Int): Int {
        val characterWidthInDp = 18f
        val paint = Paint().apply {
            textSize = characterWidthInDp.spToPx()
            typeface = Typeface.MONOSPACE
        }
        return paint.measureText("w".repeat(charactersPerLine)).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tools_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_options) {
            openPrinterSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openPrinterSettings() {
        val intent = Intent(this, SettingsPrinterActivity::class.java)
        startActivity(intent)
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

    private fun subscribeEvents() {
        printOptions.onSelect = {
            viewModel.selectTab(it)
        }

//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_claim_stub -> {
//                    viewModel.selectTab(TAB_CLAIM_STUB)
//                    true
//                }
//                R.id.menu_job_order -> {
//                    viewModel.selectTab(TAB_JOB_ORDER)
//                    true
//                }
//                R.id.menu_machine_stub -> {
//                    viewModel.selectTab(TAB_MACHINE_STUB)
//                    true
//                }
//                else -> false
//            }
//        }

        helper.setOnBluetoothStateChanged {
            viewModel.setBluetoothState(it)
        }

//        helper.setOnBluetoothAvailabilityChanged {
//            viewModel.setBluetoothAvailability(it)
//        }

        launcher.onOk = {
            it.data?.getParcelableExtra<PrinterDevice>(PRINTER_DEVICE_EXTRA)?.let(viewModel::setDevice)
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

    private fun print(formattedText: String) {
        val intent = Intent(this, PrinterService::class.java).apply {
            putExtra(PrinterService.PAYLOAD_TEXT, formattedText)
        }
        startForegroundService(intent)
    }

    private fun cancel() {
        val intent = Intent(this, PrinterService::class.java).apply {
            action = PrinterService.CANCEL_PRINT_ACTION
        }
        startForegroundService(intent)
    }

    private fun subscribeListeners() {
        viewModel.dataState.observe(this, Observer {
            when (it) {
                is JobOrderPrintViewModel.DataState.Submit -> {
                    print(it.formattedText)
                    viewModel.resetState()
                }
                is JobOrderPrintViewModel.DataState.Cancel -> {
                    cancel()
                    viewModel.resetState()
                }

                else -> {}
            }
        })

        viewModel.selectedTab.observe(this, Observer {
            printOptions.selectOption(it)
        })

        viewModel.characterLength.observe(this, Observer {
            binding.wrapper.layoutParams.width = getWidth(it) + 32
        })

        viewModel.items.observe(this, Observer {
            itemsAdapter.setData(it)
            println("data set changed")
            println(
                it.joinToString("\n") {
                    it.formattedString()
                }
            )
        })
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                PrinterService.PRINT_ACTION -> handlePrintAction(intent)
            }
        }
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
}
