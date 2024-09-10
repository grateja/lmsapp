package com.vag.lmsapp.app.reports.daily_report.job_order_items

import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

data class DailyReportJobOrderItemDetails (
    @ColumnInfo("job_order_id")
    val jobOrderId: UUID,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("item_name")
    val itemName: String,
    val quantity: Int,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    @ColumnInfo("created_at")
    val createdAt: Instant,
) {
    fun quantityStr() : String {
        return "($quantity * P$discountedPrice)"
    }
}