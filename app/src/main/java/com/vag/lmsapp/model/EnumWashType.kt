package com.vag.lmsapp.model

enum class EnumWashType(val id: Int, val value: String, val pulse: Int, val description: String, var defaultMinutes: Int, var selected: Boolean = false) {
    DELICATE(
        1,
        "Delicate Wash",
        1,
        "~21 Minutes to ~24 Minutes",
        21
    ),
    WARM(
        2,
        "Warm Wash",
        2,
        "",
        36
    ),
    COLD(
        3,
        "Cold Wash",
        2,
        "",
        36
    ),
    HOT(
        4,
        "Hot Wash",
        3,
        "",
        46
    ),
    SUPER_WASH(
        5,
        "Super Wash",
        4,
        "",
        47
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