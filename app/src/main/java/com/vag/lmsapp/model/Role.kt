package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Role(val value: String) : Parcelable {
    STAFF("staff"),
    OWNER("owner");
//    DEVELOPER("developer");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromString(key: String) : Role {
            val list = entries.toTypedArray()
            return list.first {
                it.toString() == key
            }
        }
    }
}