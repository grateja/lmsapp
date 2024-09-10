package com.vag.lmsapp.app.reports.daily_report.job_order

import androidx.room.ColumnInfo

data class DailyReportJobOrder(
    @ColumnInfo("created_count")
    val createdCount: Int,

    @ColumnInfo("total_amount")
    val totalAmount: Float,

    @ColumnInfo("returning_customer")
    val returningCustomer: Int,

    @ColumnInfo("new_customer")
    val newCustomer: Int,

    @ColumnInfo("discounted_jo_count")
    val discountedJOCount: Int,

    @ColumnInfo("discounted_jo_amount")
    val discountedJOAmount: Float
)