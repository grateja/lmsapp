package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import java.util.*

object UUIDConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun uuidFromString(string: String?): UUID? {
        return if(string == null) {
            null
        } else {
            UUID.fromString(string)
        }
    }
}