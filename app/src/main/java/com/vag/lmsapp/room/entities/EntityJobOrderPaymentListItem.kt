package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumPaymentMethod
import java.time.Instant
import java.util.*

data class EntityJobOrderPaymentListItem(
    val id: UUID,

    @ColumnInfo(name = "payment_method")
    var paymentMethod: EnumPaymentMethod,

    @ColumnInfo(name = "amount_due")
    var amountDue: Float = 0f,

    @ColumnInfo(name = "or_number")
    var orNumber: String? = null,

    @ColumnInfo("cashless_provider")
    val cashlessProvider: String?,

    @ColumnInfo("customer_id")
    val customerId: UUID,

    @ColumnInfo("name")
    val customerName: String,

    @ColumnInfo("created_at")
    val datePaid: Instant,

    @ColumnInfo("job_order_reference")
    val jobOrderReference: String,

    val sync: Boolean
) {
    fun paymentOption() : String {
        return if(paymentMethod == EnumPaymentMethod.CASH) {
            "CASH"
        } else if(paymentMethod == EnumPaymentMethod.CASHLESS) {
            cashlessProvider ?: ""
        } else {
            "CASH + $cashlessProvider"
        }
    }
}
