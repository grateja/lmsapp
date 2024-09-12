package com.vag.lmsapp.app.reports.yearly_report

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YearlyResult(
    val year: Int,
    val newCustomerCount: Int = 0,
    val jobOrderCount: Int = 0,
    val jobOrderAmount: Float = 0f,
    val expensesAmount: Float = 0f,
): Parcelable