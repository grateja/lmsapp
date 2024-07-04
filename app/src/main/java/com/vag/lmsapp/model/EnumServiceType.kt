package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumServiceType(val id: Int, val value: String, val icon: Int) : Parcelable {
    WASH(1, "Wash", R.drawable.icon_service_type_wash),
    DRY(2, "Dry", R.drawable.icon_service_type_dry);

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumServiceType? {
            return entries.find {
                it.id == id
            }
        }

        fun fromName(name: String?) : EnumServiceType? {
            return entries.find {
                it.value == name
            }
        }
    }
}