package com.vag.lmsapp.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice

class BluetoothPrinterHelper(private val activity: AppCompatActivity) {
    private val bluetoothHelper = BluetoothPermissionHelper(activity).apply {
        setOnRequestGranted {
            enableBluetooth()
        }
    }
    private val locationHelper = LocationPermissionHelper(activity).apply {
        setOnRequestGranted {
            enableLocation()
        }
    }

    private var _onBluetoothAvailabilityChanged: ((Boolean) -> Unit)? = null
    private var _onBluetoothEnabled: ((Boolean) -> Unit)? = null
    private var _onBondedDeviceLoaded: ((List<PrinterDevice>) -> Unit)? = null
    private var _onDeviceFound: ((PrinterDevice) -> Unit)? = null
    private var _onLocationStateChanged: ((Boolean) -> Unit)? = null

    fun setOnBluetoothAvailabilityChanged(event: (Boolean) -> Unit) {
        _onBluetoothAvailabilityChanged = event
        bluetoothHelper.isBluetoothSupported().let {
            _onBluetoothAvailabilityChanged?.invoke(it)
        }
    }

    fun setOnBluetoothStateChanged(event: (Boolean) -> Unit) {
        _onBluetoothEnabled = event
    }

    fun setOnBondedDeviceLoaded(event: (List<PrinterDevice>) -> Unit) {
        _onBondedDeviceLoaded = event
    }

    fun setOnDeviceFound(event: (PrinterDevice) -> Unit) {
        _onDeviceFound = event
    }

    fun setOnLocationStateChanged(event: (Boolean) -> Unit) {
        _onLocationStateChanged = event
    }

    private val receiver = object: BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    _onBluetoothEnabled?.invoke(state == BluetoothAdapter.STATE_ON)
                }
                BluetoothDevice.ACTION_FOUND -> {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.let {
                        _onDeviceFound?.invoke(PrinterDevice(it.name, it.address))
                    }
                }
                LocationManager.MODE_CHANGED_ACTION -> {
//                    val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    val locationState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        intent.getBooleanExtra(LocationManager.EXTRA_LOCATION_ENABLED, false)
                    } else {
                        false
                    }
                    _onLocationStateChanged?.invoke(locationState)
                }
            }
        }
    }

    fun registerReceiver(activity: AppCompatActivity) {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(LocationManager.MODE_CHANGED_ACTION)
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        activity.registerReceiver(receiver, filter)

        _onBluetoothEnabled?.invoke(bluetoothHelper.isEnabled())
        _onBondedDeviceLoaded?.invoke(bluetoothHelper.getBondedDevices())
        _onLocationStateChanged?.invoke(locationHelper.isLocationEnabled())
    }

    fun unregisterReceiver() {
        activity.unregisterReceiver(receiver)
    }

    fun enableBluetooth() {
        bluetoothHelper.enableBluetooth()
    }

    fun enableLocation() {
        locationHelper.enableLocationServices()
    }

    fun startDiscovery() {
        bluetoothHelper.startDiscovery()
    }
}