package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumDeliveryVehicle

data class EntityJobOrderPickupDeliveryAggrResult(
    @ColumnInfo(name = "vehicle")
    val vehicle: EnumDeliveryVehicle?,
    val count: Int,
)
