package com.vag.lmsapp.util

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections

class PrinterHelper(activity: AppCompatActivity) {
    private val COARSE_LOCATION_REQUEST_CODE = 101
    private val BLUETOOTN_ADMIN_RQ = 102
    private val BLUETOOTN_CONNECT_RQ = 103
    private val BLUETOOTN_SCAN_RQ = 104
    private val FINE_LOCATION_REQUEST_CODE = 105
    private val blueToothLauncher = ActivityLauncher(activity)
    private val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var bluetoothAdapter = bluetoothManager.adapter
    init {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_REQUEST_CODE)
            println("Permission not granted coarse location")
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), COARSE_LOCATION_REQUEST_CODE)
            println("Permission not granted coarse location")
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), BLUETOOTN_ADMIN_RQ)
            println("Permission not granted bluetooth admin")
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTN_CONNECT_RQ)
                println("Permission not granted bluetooth connect")
            }
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN), BLUETOOTN_CONNECT_RQ)
                println("Permission not granted bluetooth connect")
            }
        }

        if(!bluetoothAdapter.isEnabled) {
            val intentBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            blueToothLauncher.launch(intentBluetooth)
        } else {
            Toast.makeText(activity, "Bluetooth is required to print", Toast.LENGTH_LONG).show()
        }
    }

    fun printers(): Array<out BluetoothConnection>? {
        return BluetoothPrintersConnections().list
    }

    fun searchNearByDevice() {
        bluetoothAdapter.startDiscovery()
        println("bluetooth discovery")
    }
}