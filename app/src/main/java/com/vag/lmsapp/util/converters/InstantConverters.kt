package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import java.time.Instant

object InstantConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        if(value == null) {
            return null
        }
        return value.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        if(value == null) {
            return null
        }
        return Instant.ofEpochMilli(value)
    }
}

//object InstantConverters {
//    @TypeConverter
//    fun fromInstant(value: Instant?): Long? {
//        if (value == null) return null
//        val timeZone = ZoneId.systemDefault()
//        return value.atZone(timeZone).toInstant().toEpochMilli()
//    }
//
//    @TypeConverter
//    fun toInstant(value: Long?): Instant? {
//        if (value == null) return null
//        val timeZone = ZoneId.systemDefault()
//        return Instant.ofEpochMilli(value).atZone(timeZone).toInstant()
//    }
//}