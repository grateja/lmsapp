package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumMachineType(val id: Int, val value: String, val abbr: String, val serviceType: EnumServiceType, val icon: Int) : Parcelable {
    REGULAR_WASHER(
        1,
        "8KG Washer",
        "8KG",
        EnumServiceType.WASH,
        R.drawable.icon_washer
    ),
    REGULAR_DRYER(
        2,
        "8KG Dryer",
        "8KG",
        EnumServiceType.WASH,
        R.drawable.icon_dryer
    ),
    TITAN_WASHER(
        3,
        "12KG Washer",
        "12KG",
        EnumServiceType.DRY,
        R.drawable.icon_washer
    ),
    TITAN_DRYER(
        4,
        "12KG Dryer",
        "12KG",
        EnumServiceType.DRY,
        R.drawable.icon_dryer
    );

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