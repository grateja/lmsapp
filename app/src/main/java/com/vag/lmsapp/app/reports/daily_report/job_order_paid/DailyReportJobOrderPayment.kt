package com.vag.lmsapp.app.reports.daily_report.job_order_paid

import androidx.room.ColumnInfo
import java.text.NumberFormat
import java.util.Locale

data class DailyReportJobOrderPayment(
    @ColumnInfo(name = "provider")
    val provider: String?,

    @ColumnInfo(name = "count")
    val count: Int,

    @ColumnInfo(name = "amount")
    val amount: Float,
) {
    override fun toString() : String {
        val amount = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount))
        return "($count) $provider - $amount"
    }
}