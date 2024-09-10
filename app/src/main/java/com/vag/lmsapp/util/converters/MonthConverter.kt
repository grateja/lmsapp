package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.app.reports.monthly_report.EnumMonths
import com.vag.lmsapp.model.Role

object MonthConverter {
    @TypeConverter
    fun fromMonth(month: EnumMonths?): String? {
        return month?.monthNumber
    }

    @TypeConverter
    fun toMonth(monthNumber: String?): EnumMonths? {
        return EnumMonths.entries.find { it.monthNumber == monthNumber }
    }
}