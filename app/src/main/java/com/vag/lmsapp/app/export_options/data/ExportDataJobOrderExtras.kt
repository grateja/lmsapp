package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import java.time.Instant

data class ExportDataJobOrderExtras(
    @ColumnInfo("created_at")
    val createdAt: Instant,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("extras_name")
    val extrasName: String,

    @ColumnInfo("category")
    val category: String,

    @ColumnInfo("quantity")
    val quantity: Int,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double,
)