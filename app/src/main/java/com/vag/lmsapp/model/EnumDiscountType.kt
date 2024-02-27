package com.vag.lmsapp.model

enum class EnumDiscountType(val value: String) {
    FIXED("Fixed"),
    PERCENTAGE("Percentage");

    override fun toString() : String {
        return value
    }

    companion object {
        fun fromString(name: String?) : EnumDiscountType? {
            return entries.find {
                it.value == name
            }
        }
    }
}