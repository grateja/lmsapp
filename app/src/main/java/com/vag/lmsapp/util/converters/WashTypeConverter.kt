package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumWashType

object WashTypeConverter {
    @TypeConverter
    fun fromWashType(washType: EnumWashType?): Int? {
        return washType?.id
    }

    @TypeConverter
    fun washTypeFromInt(id: Int?): EnumWashType? {
        return if(id == null) {
            null
        } else {
            EnumWashType.fromId(id)
        }
    }
}