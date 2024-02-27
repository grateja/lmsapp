package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter
import com.vag.lmsapp.model.EnumDeliveryVehicle

object DeliveryVehicleConverter {
    @TypeConverter
    fun fromDeliveryVehicle(deliveryVehicle: EnumDeliveryVehicle?): Int? {
        return deliveryVehicle?.id
    }

    @TypeConverter
    fun deliveryVehicleFromInt(id: Int?): EnumDeliveryVehicle? {
        return if(id == null) {
            null
        } else {
            EnumDeliveryVehicle.fromId(id)
        }
    }
}