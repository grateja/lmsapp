package com.vag.lmsapp.app.reports.calendar

sealed class DailyPlaceHolder {
    data class HasResult(val result: DailyResult): DailyPlaceHolder()
    data class EmptyResult(val day: Int): DailyPlaceHolder()
    data object Offset: DailyPlaceHolder()
    data class Header(val dayOfWeek: EnumDayOfWeek): DailyPlaceHolder()
}