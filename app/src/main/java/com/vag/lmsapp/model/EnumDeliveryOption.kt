package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumDeliveryOption(val id: Int, val value: String, val charge: Int, var selected: Boolean = false) : Parcelable {
    DELIVERY_ONLY(1, "Delivery Only", 1),
    PICKUP_ONLY(2,"Pickup Only", 1),
    PICKUP_AND_DELIVERY(3,"Pickup & Delivery", 2);

    override fun toString() : String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumDeliveryOption? {
            return entries.find {
                it.id == id
            }
        }
    }
}