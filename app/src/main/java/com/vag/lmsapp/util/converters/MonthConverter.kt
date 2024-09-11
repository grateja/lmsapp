package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.app.reports.monthly_report.EnumMonths

object MonthConverter {
    @TypeConverter
    fun fromMonth(month: EnumMonths?): String? {
        return month?.monthNumberStr
    }

    @TypeConverter
    fun toMonth(monthNumber: String?): EnumMonths? {
        return EnumMonths.entries.find { it.monthNumberStr == monthNumber }
    }
}