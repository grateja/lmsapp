package com.vag.lmsapp.settings

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterSettingsRepository

@Inject
constructor(
    private val dao: SettingsDao
) : BaseSettingsRepository(dao) {
    companion object {
        const val PRINTER_NAME = "printerName"
        const val PRINTER_ADDRESS = "printerAddress"
        const val PRINTER_WIDTH = "printerWidth"
        const val PRINTER_CHARACTERS_PER_LINE = "printerCharactersPerLine"
        const val JOB_ORDER_DISCLAIMER = "disclaimer"
        const val SHOW_DISCLAIMER = "showDisclaimer"

        const val SHOW_PACKAGE_ITEMS = "showPackageItems"
        const val SHOW_PACKAGE_PRICE = "showPackagePrice"
        const val SHOW_JO_ITEMIZED = "showJoItemized"
        const val SHOW_JO_PRICES = "showJoPrices"
        const val SHOW_CLAIM_STUB_ITEMIZED = "showClaimStubItemized"
        const val SHOW_CLAIM_STUB_PRICES = "showClaimStubPrices"
    }
    val printerName = getAsLiveData(PRINTER_NAME, "No printer selected")
    val printerAddress = getAsLiveData(PRINTER_ADDRESS, "Not set")
    val printerWidth = getAsLiveData(PRINTER_WIDTH, 58f)
    val printerCharactersPerLine = getAsLiveData(
        PRINTER_CHARACTERS_PER_LINE, 32)
    val jobOrderDisclaimer = getAsLiveData(JOB_ORDER_DISCLAIMER,
        "Disclaimer: This document is provided for informational purposes only. It does not constitute an official receipt and should not be considered proof of payment.")
    val showDisclaimer = getAsLiveData(SHOW_DISCLAIMER, true)

    val showJoItemized = getAsLiveData(SHOW_JO_ITEMIZED, true)
    val showJoPrices = getAsLiveData(SHOW_JO_PRICES, true)

    val showPackageItems = getAsLiveData(SHOW_PACKAGE_ITEMS, true)

    val showClaimStubItemized = getAsLiveData(SHOW_CLAIM_STUB_ITEMIZED, true)
    val showClaimStubJoPrices = getAsLiveData(SHOW_CLAIM_STUB_PRICES, true)

    suspend fun getPrinterAddress() = getValue(PRINTER_ADDRESS, "")
    suspend fun getPrinterWidth() = getValue(PRINTER_WIDTH, 58f)
    suspend fun getPrinterCharacters() = getValue(PRINTER_CHARACTERS_PER_LINE, 32)

    suspend fun updatePrinterName(value: String?) {
        update(value, PRINTER_NAME)
    }

    suspend fun updatePrinterAddress(value: String?) {
        update(value, PRINTER_ADDRESS)
    }

    suspend fun updatePrinterWidth(value: Float?) {
        update(value, PRINTER_WIDTH)
    }

    suspend fun updatePrinterCharacters(value: Int?) {
        update(value, PRINTER_CHARACTERS_PER_LINE)
    }

    suspend fun updateJobOrderItemized(value: Boolean) {
        update(value, SHOW_JO_ITEMIZED)
    }

    suspend fun updateJobOrderShowItemPrice(value: Boolean) {
        update(value, SHOW_JO_PRICES)
    }

    suspend fun updateClaimStubItemized(value: Boolean) {
        update(value, SHOW_CLAIM_STUB_ITEMIZED)
    }

    suspend fun updateClaimStubShowItemPrice(value: Boolean) {
        update(value, SHOW_CLAIM_STUB_PRICES)
    }

    suspend fun updateShowDisclaimer(value: Boolean) {
        update(value, SHOW_DISCLAIMER)
    }

    suspend fun updateDisclaimer(value: String) {
        update(value, JOB_ORDER_DISCLAIMER)
    }
}