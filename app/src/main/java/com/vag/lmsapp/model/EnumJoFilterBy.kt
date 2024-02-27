package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumJoFilterBy(val itemIndex: Int, val value: String) : Parcelable {
    DATE_PAID(0, "paid"),
    DATE_CREATED(1, "created");

    companion object {
        fun fromString(value: String) : EnumJoFilterBy? {
            return entries.find {
                it.value == value
            }
        }
    }

    override fun toString(): String {
        return value
    }
}