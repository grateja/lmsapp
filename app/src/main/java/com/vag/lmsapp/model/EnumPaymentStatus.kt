package com.vag.lmsapp.model

enum class EnumPaymentStatus(val id: Int, val prompt: String) {
    PAID(0, "Paid only"),
    UNPAID(1, "Unpaid only"),
    ALL(2, "Paid and Unpaid");

    companion object {
        fun fromId(id: Int?) : EnumPaymentStatus? {
            return entries.find {
                it.id == id
            }
        }
    }

    override fun toString(): String {
        return prompt
    }
}