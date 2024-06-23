package com.vag.lmsapp.app.joborders

data class JobOrderItemMinimal(
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
