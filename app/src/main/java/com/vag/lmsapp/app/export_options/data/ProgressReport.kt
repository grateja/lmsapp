package com.vag.lmsapp.app.export_options.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgressReport(
    val entity: String,
    val index: Int,
    val count: Int
): Parcelable {
    override fun toString(): String {
        return "$entity: ($index/$count)"
    }
}