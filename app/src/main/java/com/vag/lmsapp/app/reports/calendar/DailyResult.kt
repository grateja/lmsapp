package com.vag.lmsapp.app.reports.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class DailyResult(
    val date: LocalDate,
    val newCustomerCount: Int,
    val jobOrderCount: Int,
    val jobOrderAmount: Float,
    val expensesAmount: Float,
): Parcelable
