package com.vag.lmsapp.app.app_settings.developer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrinterSettings(
    val name: String?,
    val address: String?,
    val width: Float,
    val character: Int
) : Parcelable