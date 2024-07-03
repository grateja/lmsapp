package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumMachineType(val id: Int, val value: String, val abbr: String/*, val icon: Int*/) : Parcelable {
    REGULAR(
        1,
        "Giant",
        "8KG",
//        R.drawable.icon_washer
    ),
//    REGULAR_DRYER(
//        2,
//        "8KG Dryer",
//        "8KG",
//        R.drawable.icon_dryer
//    ),
    TITAN(
        2,
        "Titan",
        "12KG",
//        R.drawable.icon_washer
    );//,
//    TITAN_DRYER(
//        4,
//        "12KG Dryer",
//        "12KG",
//        R.drawable.icon_dryer
//    );

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumMachineType? {
            return entries.find {
                it.id == id
            }
        }

        fun fromName(name: String?) : EnumMachineType? {
            return entries.find {
                it.value.uppercase() == name?.uppercase()
            }
        }
    }
}