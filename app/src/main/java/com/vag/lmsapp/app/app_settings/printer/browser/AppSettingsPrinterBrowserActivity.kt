package com.vag.lmsapp.app.app_settings.printer.browser

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.app_settings.printer.SettingsPrinterActivity
import com.vag.lmsapp.databinding.ActivityAppSettingsPrinterBrowserBinding
import com.vag.lmsapp.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsPrinterBrowserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppSettingsPrinterBrowserBinding
    private val viewModel: PrinterBrowserViewModel by viewModels()
    private val helper = BluetoothPrinterHelper(this)
    private val foundDeviceAdapter = Adapter<PrinterDevice>(R.layout.recycler_item_printer_device)
    private val savedDeviceAdapter = Adapter<PrinterDevice>(R.layout.recycler_item_printer_device)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_printer_browser)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val formattedStatement = getString(R.string.permission_prompt)
        binding.textBluetoothPermissionPrompt.text = Html.fromHtml(formattedStatement, Html.FROM_HTML_MODE_COMPACT)

        binding.devices.adapter = savedDeviceAdapter

        subscribeEvents()
        subscribeListeners()
    }
    override fun onResume() {
        super.onResume()
        helper.registerReceiver(this)
    }
    override fun onPause() {
        super.onPause()
        helper.unregisterReceiver()
    }

    private fun subscribeListeners() {
        viewModel.devices.observe(this, Observer {
            savedDeviceAdapter.setData(it)
        })
    }

    private fun submit(printerDevice: PrinterDevice) {
        val intent = Intent(intent.action).apply {
            putExtra(SettingsPrinterActivity.PRINTER_DEVICE_EXTRA, printerDevice)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
    private fun subscribeEvents() {
        binding.swipeRefresh.setOnRefreshListener {
            helper.startDiscovery()
        }
        binding.cardBluetooth.setOnClickListener {
            helper.enableBluetooth()
        }
        binding.checkboxBluetoothState.setOnClickListener {
            helper.enableBluetooth()
        }

        binding.buttonLocationServiceToggle.setOnClickListener {
            helper.enableLocation()
        }

        helper.apply {
            setOnBluetoothStateChanged {
                viewModel.setBluetoothState(it)
            }
            setOnBondedDeviceLoaded {
                viewModel.setDevices(it)
            }
            setOnDeviceFound {
                viewModel.addFoundDevice(it)
                binding.swipeRefresh.isRefreshing = false
            }
            setOnLocationStateChanged {
                viewModel.setLocationState(it)
                helper.startDiscovery()
            }
        }
        foundDeviceAdapter.onItemClick = {
            submit(it)
        }
        savedDeviceAdapter.onItemClick = {
            submit(it)
        }
    }
}