package com.vag.lmsapp.app.export_options.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Selections(
    val key: String,
    val text: String,
    var count: Int = 0,
    var selected: Boolean = false
): Parcelable
