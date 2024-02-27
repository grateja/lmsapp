package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumPrintState : Parcelable {
    READY,
    STARTED,
    CANCELED,
    FINISHED,
    ERROR
}