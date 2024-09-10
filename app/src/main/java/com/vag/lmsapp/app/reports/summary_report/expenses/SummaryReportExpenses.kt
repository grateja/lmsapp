package com.vag.lmsapp.app.reports.summary_report.expenses

import java.text.NumberFormat
import java.util.Locale

data class SummaryReportExpenses(
    var remarks: String,
    var amount: Double
) {
    override fun toString(): String {
        val amount = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount))
        return "$remarks - $amount"
    }
}
