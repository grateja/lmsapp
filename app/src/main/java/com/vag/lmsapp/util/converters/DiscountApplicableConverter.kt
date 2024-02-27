package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumDiscountApplicable

object DiscountApplicableConverter {
    @TypeConverter
    fun fromDiscountApplicable(discounts: List<EnumDiscountApplicable>): String? {
        return EnumDiscountApplicable.toIds(discounts)
    }

    @TypeConverter
    fun toDiscountApplicable(string: String?): List<EnumDiscountApplicable>? {
        return if(string == null) {
            listOf()
        } else {
            EnumDiscountApplicable.fromIds(string)
        }
    }
}