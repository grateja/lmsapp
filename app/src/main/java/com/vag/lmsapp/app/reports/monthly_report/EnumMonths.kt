package com.vag.lmsapp.app.reports.monthly_report

enum class EnumMonths(val monthNumber: Int, val monthNumberStr: String, val monthName: String) {
    JANUARY(1, "01", "January"),
    FEBRUARY(2, "02", "February"),
    MARCH(3, "03", "March"),
    APRIL(4, "04", "April"),
    MAY(5, "05", "May"),
    JUNE(6, "06", "June"),
    JULY(7, "07", "July"),
    AUGUST(8, "08", "August"),
    SEPTEMBER(9, "09", "September"),
    OCTOBER(10, "10", "October"),
    NOVEMBER(11, "11", "November"),
    DECEMBER(12, "12", "December");

    companion object {
        fun fromNumber(number: String): EnumMonths? {
            return entries.find { it.monthNumberStr == number }
        }
    }
}
