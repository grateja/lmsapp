package com.vag.lmsapp.model

enum class EnumMeasureUnit(val value: String) {
    PCS("pcs."),
    SACHET("Sachet"),
    MILLILITER("mL"),
    LOAD("Load"),
    PACK("Pack");
    override fun toString() : String {
        return value
    }
    companion object {
        fun fromString(key: String) : EnumMeasureUnit? {
            val list = entries.toTypedArray()
            return list.find {
                it.toString() == key
            }
        }
    }
}