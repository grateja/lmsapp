package com.vag.lmsapp.app.daily_report.expenses

import java.text.NumberFormat
import java.util.Locale

data class DailyReportExpenses(
    var remarks: String,
    var amount: Double
) {
    override fun toString(): String {
        val amount = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount))
        return "$remarks - $amount"
    }
}
