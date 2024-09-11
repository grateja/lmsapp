package com.vag.lmsapp.app.reports.monthly_report

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.YearMonth

@Parcelize
data class MonthlyResult(
    val year: Int,
    val month: EnumMonths,
    val newCustomerCount: Int = 0,
    val jobOrderCount: Int = 0,
    val jobOrderAmount: Float = 0f,
    val expensesAmount: Float = 0f,
): Parcelable {
    fun daysInMonth(): Int {
        return YearMonth.of(year, month.monthNumber).lengthOfMonth()
    }
}
