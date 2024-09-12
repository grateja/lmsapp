package com.vag.lmsapp.app.reports.monthly_report

sealed class MonthlyPlaceHolder {
    data class HasResult(val result: MonthlyResult): MonthlyPlaceHolder()
    data class EmptyResult(val month: EnumMonths): MonthlyPlaceHolder()
}