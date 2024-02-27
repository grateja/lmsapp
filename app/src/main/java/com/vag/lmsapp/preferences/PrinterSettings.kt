package com.vag.lmsapp.preferences

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrinterSettings(
    val name: String?,
    val address: String?,
//    val dpi: Int,
    val width: Float,
    val character: Int
) : Parcelable