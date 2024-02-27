package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumProductType

object ProductTypeConverter {
    @TypeConverter
    fun fromProductType(productType: EnumProductType?): Int? {
        return productType?.id
    }

    @TypeConverter
    fun productTypeFromId(id: Int?): EnumProductType? {
        return if(id == null) {
            null
        } else {
            EnumProductType.fromId(id)
        }
    }
}