package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumDeliveryVehicle

@Entity(tableName = "delivery_profiles")
data class EntityDeliveryProfile(
    var vehicle: EnumDeliveryVehicle,

    @ColumnInfo(name = "base_fare")
    var baseFare: Float,

    @ColumnInfo(name = "price_per_km")
    var pricePerKm: Float = 0f,

    @ColumnInfo(name = "min_distance")
    var minDistance: Float = 1f,
) : BaseEntity() {
    constructor(): this(EnumDeliveryVehicle.MOTORCYCLE, 0f, 0f)
}