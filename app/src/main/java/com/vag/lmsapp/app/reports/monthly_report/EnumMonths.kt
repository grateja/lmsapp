package com.vag.lmsapp.app.reports.monthly_report

enum class EnumMonths(val monthNumber: String, val monthName: String) {
    JANUARY("01", "January"),
    FEBRUARY("02", "February"),
    MARCH("03", "March"),
    APRIL("04", "April"),
    MAY("05", "May"),
    JUNE("06", "June"),
    JULY("07", "July"),
    AUGUST("08", "August"),
    SEPTEMBER("09", "September"),
    OCTOBER("10", "October"),
    NOVEMBER("11", "November"),
    DECEMBER("12", "December");

    companion object {
        fun fromNumber(number: String): EnumMonths? {
            return entries.find { it.monthNumber == number }
        }
    }
}
