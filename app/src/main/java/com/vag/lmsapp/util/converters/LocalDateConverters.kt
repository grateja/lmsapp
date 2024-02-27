package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import java.time.LocalDate

object LocalDateConverters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        if(value == null) {
            return null
        }
        return value.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        try {
            if(value != null) {
                return LocalDate.parse(value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}