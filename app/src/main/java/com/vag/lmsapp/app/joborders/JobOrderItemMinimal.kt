package com.vag.lmsapp.app.joborders

import java.util.UUID

data class JobOrderItemMinimal(
    val id: UUID,
    val quantity: Int,
    val name: String,
    val discountedPrice: Float
) {
    fun quantityStrAbbr() : String {
        return "($quantity * $discountedPrice)"
    }

    fun total() : Float {
        return quantity * discountedPrice
    }
}
