package com.vag.lmsapp.app.joborders

data class JobOrderItemMinimal(
    val quantity: Float,
    val name: String,
    val price: Float
) {
    fun quantityStrAbbr() : String {
        return "($quantity * $price)"
    }

    fun total() : Float {
        return quantity * price
    }
}
