package com.vag.lmsapp.app.app_settings.printer.browser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.settings.PrinterSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrinterBrowserViewModel

@Inject
constructor(
    val dataStoreRepository: PrinterSettingsRepository
) : ViewModel()
{
    val devices = MutableLiveData<List<PrinterDevice>>()
    private val currentDeviceAddress = dataStoreRepository.printerAddress
    val bluetoothEnabled = MutableLiveData(false)
    val locationEnabled = MutableLiveData(false)

    fun setDevices(devices: List<PrinterDevice>) {
        val address = currentDeviceAddress.value
        devices.find { it.macAddress == address }?.selected = true
        this.devices.value = devices
    }

    fun setBluetoothState(state: Boolean) {
        bluetoothEnabled.value = state
    }
    fun addFoundDevice(device: PrinterDevice) {
        val currentDevices = devices.value.orEmpty().toMutableList()
        val existingDevice = currentDevices.find { it.macAddress == device.macAddress }

        if (existingDevice != null) {
            existingDevice.inRange = true
        } else {
            device.inRange = true
            currentDevices.add(device)
        }

        devices.value = currentDevices
    }
    fun setLocationState(state: Boolean) {
        locationEnabled.value = state
    }
}