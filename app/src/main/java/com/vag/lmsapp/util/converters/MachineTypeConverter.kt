package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumMachineType

object MachineTypeConverter {
    @TypeConverter
    fun fromMachineType(machineType: EnumMachineType?): Int? {
        return machineType?.id
    }

    @TypeConverter
    fun machineTypeFromInt(id: Int?): EnumMachineType? {
        return if(id == null) {
            null
        } else {
            EnumMachineType.fromId(id)
        }
    }
}