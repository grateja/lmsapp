package com.vag.lmsapp.app.app_settings.printer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.SettingsViewModel
import com.vag.lmsapp.app.app_settings.printer.browser.PrinterDevice
import com.vag.lmsapp.settings.PrinterSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterSettingsViewModel

@Inject
constructor(
    private val repository: PrinterSettingsRepository,
    @ApplicationContext context: Context
) : SettingsViewModel(repository) {
    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    val printerName = repository.printerName
    val macAddress = repository.printerAddress
    val width = repository.printerWidth
    val charactersPerLine = repository.printerCharactersPerLine

    val jobOrderItemized = repository.showJoItemized
    val jobOrderShowItemPrice = repository.showJoPrices
    val claimStubItemized = repository.showClaimStubItemized
    val claimStubShowItemPrice = repository.showClaimStubJoPrices

    val showDisclaimer = repository.showDisclaimer
    val disclaimerText = repository.jobOrderDisclaimer

    fun updateJobOrderItemized(checked: Boolean) {
        viewModelScope.launch {
            repository.updateJobOrderItemized(checked)
        }
    }

    fun updateJobOrderShowItemPrice(checked: Boolean) {
        viewModelScope.launch {
            repository.updateJobOrderShowItemPrice(checked)
        }
    }

    fun updateClaimStubItemized(checked: Boolean) {
        viewModelScope.launch {
            repository.updateClaimStubItemized(checked)
        }
    }

    fun updateClaimStubShowItemPrice(checked: Boolean) {
        viewModelScope.launch {
            repository.updateClaimStubShowItemPrice(checked)
        }
    }

    fun openPrinterBrowser() {
        _dataState.value = DataState.OpenPrinterBrowser(
            PrinterDevice(
                printerName.value, macAddress.value
            )
        )
    }

    fun setPrinterDevice(printerDevice: PrinterDevice) {
        viewModelScope.launch {
            repository.updatePrinterName(printerDevice.deviceName)
            repository.updatePrinterAddress(printerDevice.macAddress)
        }
    }

    fun openPrinterWidth() {
        val width = width.value ?: 32f
        _dataState.value = DataState.OpenPrinterWidth(width)
    }

    fun openPrinterCharactersPerLine() {
        val charactersPerLine = charactersPerLine.value ?: 58
        _dataState.value = DataState.OpenPrinterCharactersPerLine(charactersPerLine)
    }

    fun setPrinterWidth(width: Float) {
        viewModelScope.launch {
            repository.updatePrinterWidth(width)
        }
    }

    fun setPrinterCharactersPerLine(charactersPerLine: Int) {
        viewModelScope.launch {
            repository.updatePrinterCharacters(charactersPerLine)
        }
    }

    fun openDisclaimer() {
        (disclaimerText.value ?: "").let {
            _dataState.value = DataState.OpenDisclaimer(it)
        }
    }

    fun updateDisclaimer(disclaimer: String?) {
        viewModelScope.launch {
            if(disclaimer == null) {
                repository.updateShowDisclaimer(false)
            } else {
                repository.updateDisclaimer(disclaimer)
            }
        }
    }

    fun updateShowDisclaimer(checked: Boolean) {
        viewModelScope.launch {
            repository.updateShowDisclaimer(checked)
        }
    }

    sealed class DataState {
        object StateLess: DataState()
        data class OpenPrinterBrowser(val currentPrinter: PrinterDevice) : DataState()
        data class OpenPrinterWidth(val width: Float): DataState()
        data class OpenPrinterCharactersPerLine(val charactersPerLine: Int?): DataState()
        data class OpenDisclaimer(val text: String): DataState()
        data class StartTestPrint(val payload: String): DataState()
        object Save: DataState()
    }
}