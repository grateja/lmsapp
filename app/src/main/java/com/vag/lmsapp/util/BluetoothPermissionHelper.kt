package com.vag.lmsapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice

class BluetoothPermissionHelper(private val activity: AppCompatActivity) : PermissionHelper(activity) {
    override var permissions: Array<String>
        get() = arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            } else {
                emptyArray()
            }
        )
        set(_) {}

//    private var _onBluetoothEnabled: ((Boolean) -> Unit)? = null
//    private var _onBondedDeviceLoaded: ((List<PrinterDevice>) -> Unit)? = null
//    private var _onDeviceFound: ((PrinterDevice) -> Unit)? = null

//    fun setOnBondedDeviceLoaded(event: (List<PrinterDevice>) -> Unit) {
//        _onBondedDeviceLoaded = event
//    }
//
//    fun setOnDeviceFound(event: (PrinterDevice) -> Unit) {
//        _onDeviceFound = event
//    }

    private val bluetoothStateLauncher = ActivityLauncher(activity).apply {
        onOk = {
            val state = it.data?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
            if (state == BluetoothAdapter.STATE_ON) {
                getBondedDevices()
            }
        }
    }

    private val bluetoothManager: BluetoothManager by lazy {
        activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

//    fun checkBluetooth() {
//        _onBluetoothEnabled?.invoke(bluetoothAdapter.isEnabled)
//        getBondedDevices()
//    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if(hasPermissions()) {
            println("Start discovery")
            bluetoothAdapter?.startDiscovery()
        } else {
            println("permission not granted")
        }
    }

//    fun setOnBluetoothStateChanged(event: (Boolean) -> Unit) {
//        _onBluetoothEnabled = event
//    }

    fun enableBluetooth() {
        if (!hasPermissions()) {
            requestPermission()
        } else {
            val intentBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothStateLauncher.launch(intentBluetooth)
        }
    }

    @SuppressLint("MissingPermission")
    fun getBondedDevices(): List<PrinterDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.map { device ->
                PrinterDevice(device.name, device.address)
            }?.toList() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

//    override fun requestGranted() {
//        super.requestGranted()
//        enableBluetooth()
//    }

    fun isEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true && hasPermissions()
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

//    private val receiver = object : BroadcastReceiver() {
//        @SuppressLint("MissingPermission")
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when(intent?.action) {
//                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
//                    _onBluetoothEnabled?.invoke(state == BluetoothAdapter.STATE_ON)
//                }
//                BluetoothDevice.ACTION_FOUND -> {
//                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.let {
//                        _onDeviceFound?.invoke(PrinterDevice(it.name, it.address))
//                    }
//                }
//            }
//        }
//    }

//    fun registerReceiver(activity: AppCompatActivity) {
//        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
//            addAction(BluetoothDevice.ACTION_FOUND)
//            addAction(LocationManager.MODE_CHANGED_ACTION)
//        }
//        activity.registerReceiver(receiver, filter)
//    }
//
//    fun unregisterReceiver() {
//        activity.unregisterReceiver(receiver)
//    }
}