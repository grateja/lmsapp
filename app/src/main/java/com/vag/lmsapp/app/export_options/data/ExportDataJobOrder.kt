package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import java.time.Instant

data class ExportDataJobOrder(
    @ColumnInfo("created_at")
    val createdAt: Instant,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("subtotal")
    val subtotal: Double,

    @ColumnInfo("discount_in_peso")
    val discountInPeso: Double,

    @ColumnInfo("discount_str")
    val discountStr: String?,

    @ColumnInfo("discounted_amount")
    val discountedAmount: Double,

    @ColumnInfo("date_paid")
    val datePaid: Instant?,

    @ColumnInfo("payment_method")
    val paymentMethod: String?,

    @ColumnInfo("received_by")
    val receivedBy: String?,
)
