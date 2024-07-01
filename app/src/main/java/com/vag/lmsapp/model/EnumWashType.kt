package com.vag.lmsapp.model

import com.vag.lmsapp.R

enum class EnumWashType(val id: Int, val value: String, val pulse: Int, val description: String, var defaultMinutes: Int, val icon: Int, var selected: Boolean = false) {
    DELICATE(
        1,
        "Delicate Wash",
        1,
        "~21 Minutes to ~24 Minutes",
        21,
        R.drawable.icon_wash_type_delicate
    ),
    WARM(
        2,
        "Warm Wash",
        2,
        "",
        36,
        R.drawable.icon_wash_type_warm
    ),
    COLD(
        3,
        "Cold Wash",
        2,
        "",
        36,
        R.drawable.icon_wash_type_cold
    ),
    HOT(
        4,
        "Hot Wash",
        3,
        "",
        46,
        R.drawable.icon_wash_type_hot
    ),
    SUPER_WASH(
        5,
        "Super Wash",
        4,
        "",
        47,
        R.drawable.icon_wash_type_super
    );

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumWashType? {
            return entries.find {
                it.id == id
            }
        }

        fun fromName(name: String?) : EnumWashType? {
            return entries.find {
                it.value.uppercase() == name?.uppercase()
            }
        }
    }
}