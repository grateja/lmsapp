package com.vag.lmsapp.app.reports.daily_report.job_order_paid

import androidx.room.ColumnInfo

data class DailyReportJobOrderPaymentSummary(
    @ColumnInfo("cash_count")
    val cashCount: Int = 0,

    @ColumnInfo("cash_amount")
    val cashAmount: Float = 0f,

    @ColumnInfo("cashless_count")
    val cashlessCount: Int = 0,

    @ColumnInfo("cashless_amount")
    val cashlessAmount: Float = 0f,

    @ColumnInfo("total_count")
    val totalCount: Int = 0,

    @ColumnInfo("total_amount")
    val totalAmount: Float = 0f,
)