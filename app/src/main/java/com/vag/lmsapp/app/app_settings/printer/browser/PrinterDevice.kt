package com.vag.lmsapp.app.app_settings.printer.browser

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrinterDevice(
    val deviceName: String?,
    val macAddress: String?,
    var selected: Boolean = false,
    var inRange: Boolean = false
) : Parcelable