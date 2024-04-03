package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumCRUDAction
import com.vag.lmsapp.model.EnumDeliveryOption

object CrudActionConverter {
    @TypeConverter
    fun fromEnumCrudAction(crudAction: EnumCRUDAction?): Int? {
        return crudAction?.id
    }

    @TypeConverter
    fun toEnumCrudActionInt(id: Int?): EnumCRUDAction? {
        return if(id == null) {
            null
        } else {
            EnumCRUDAction.fromId(id)
        }
    }
}