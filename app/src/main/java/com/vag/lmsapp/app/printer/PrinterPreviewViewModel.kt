package com.vag.lmsapp.app.printer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.model.EnumPrintState
import com.vag.lmsapp.model.PrinterItem
import com.vag.lmsapp.settings.PrinterSettingsRepository
import com.vag.lmsapp.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterPreviewViewModel

@Inject
constructor(
    private val printerSettings: PrinterSettingsRepository
): ViewModel() {
    private val _dataState = MutableLiveData<DataState<String>>()
    val dataState: LiveData<DataState<String>> = _dataState

    val characterLength = printerSettings.printerCharactersPerLine
    val printerName = printerSettings.printerName

    private val _permitted = MutableLiveData<Boolean>()
    val permitted: LiveData<Boolean> = _permitted

    private val _printState = MutableLiveData(EnumPrintState.READY)

    private val _printerItems = MutableLiveData<List<PrinterItem>>()
    val printerItems: LiveData<List<PrinterItem>> = _printerItems

    private val _bluetoothEnabled = MutableLiveData(false)
    val bluetoothEnabled: LiveData<Boolean> = _bluetoothEnabled

    val canPrint = MediatorLiveData<Boolean>().apply {
        fun update() {
            val enabled = _bluetoothEnabled.value ?: false
            val printState = _printState.value != EnumPrintState.STARTED
            val printerName = printerName.value != "Not set"
            value = enabled && printState && printerName
        }
        addSource(_bluetoothEnabled) {update()}
        addSource(_printState) {update()}
        addSource(printerName) {update()}
    }

    val buttonPrimaryAction = MediatorLiveData<String>().apply {
        addSource(_printState) {
            value = _printState.value?.let {
                when (it) {
                    EnumPrintState.STARTED -> "CANCEL"
                    EnumPrintState.ERROR -> "RETRY"
                    else -> "CONTINUE"
                }
            } ?: "CONTINUE"
        }
    }

    fun setPrinterItems(printerItems: List<PrinterItem>) {
        _printerItems.value = printerItems
    }

    fun setPermissionStatus(status: Boolean) {
        _permitted.value = status
    }

    fun setPrintState(printState: EnumPrintState) {
        _printState.value = printState
    }

    fun setBluetoothState(state: Boolean) {
        _bluetoothEnabled.value = state
    }

    fun setDevice(device: PrinterDevice?) {
        viewModelScope.launch {
            printerSettings.update(device?.deviceName, PrinterSettingsRepository.PRINTER_NAME)
            printerSettings.update(device?.macAddress, PrinterSettingsRepository.PRINTER_ADDRESS)
        }
    }

    fun print() {
        if(_printState.value == EnumPrintState.STARTED) {
            _dataState.value = DataState.Invalidate("Print canceled")
            return
        }

        val formattedText = _printerItems.value.let {
            it?.joinToString ("\n") { it.formattedString() }
        } ?: ""
        _dataState.value = DataState.Submit(formattedText)
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }
}