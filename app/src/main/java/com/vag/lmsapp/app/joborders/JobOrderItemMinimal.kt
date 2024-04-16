package com.vag.lmsapp.app.joborders

data class JobOrderItemMinimal(
    val quantity: Int,
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
