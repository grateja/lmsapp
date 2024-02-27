package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumDeliveryOption

object DeliveryOptionConverter {
    @TypeConverter
    fun fromDeliveryOption(deliveryOption: EnumDeliveryOption?): Int? {
        return deliveryOption?.id
    }

    @TypeConverter
    fun deliveryOptionFromInt(id: Int?): EnumDeliveryOption? {
        return if(id == null) {
            null
        } else {
            EnumDeliveryOption.fromId(id)
        }
    }
}