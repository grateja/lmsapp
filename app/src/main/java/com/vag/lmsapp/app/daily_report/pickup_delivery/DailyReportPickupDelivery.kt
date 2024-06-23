package com.vag.lmsapp.app.daily_report.pickup_delivery

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumDeliveryVehicle

data class DailyReportPickupDelivery(
    val count: Int,
    val distance: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double,

    val vehicle: EnumDeliveryVehicle? = null,
) {
    override fun toString(): String {
        return "($count) $vehicle($distance km)"
    }
}