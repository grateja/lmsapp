package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumDiscountType

object DiscountTypeConverter {
    @TypeConverter
    fun fromDiscountType(discountApplicable: EnumDiscountType?): String? {
        return discountApplicable?.toString()
    }

    @TypeConverter
    fun toDiscountApplicable(string: String?): EnumDiscountType? {
        return if(string == null) {
            null
        } else {
            EnumDiscountType.fromString(string)
        }
    }
}