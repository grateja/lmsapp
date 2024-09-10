package com.vag.lmsapp.app.reports.monthly_report

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonthlyResult(
    val month: EnumMonths,
    val newCustomerCount: Int,
    val jobOrderCount: Int,
    val jobOrderAmount: Float,
    val expensesAmount: Float,
): Parcelable
