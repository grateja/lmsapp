package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumActionPermission

object ActionPermissionConverter {
    @TypeConverter
    fun fromActionPermission(permissions: List<EnumActionPermission>?): String? {
        return permissions?.let {
            EnumActionPermission.toIds(it)
        }
    }

    @TypeConverter
    fun toActionPermission(string: String?): List<EnumActionPermission>? {
        return if(string == null) {
            return null
        } else {
            EnumActionPermission.fromIds(string)
        }
    }
}