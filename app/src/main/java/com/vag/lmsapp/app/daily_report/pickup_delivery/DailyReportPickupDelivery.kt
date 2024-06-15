package com.vag.lmsapp.app.daily_report.pickup_delivery

import com.vag.lmsapp.model.EnumDeliveryVehicle

data class DailyReportPickupDelivery(
    val count: Int,
    val distance: Float,
    val vehicle: EnumDeliveryVehicle? = null,
) {
    override fun toString(): String {
        return "($count) $vehicle($distance km)"
    }
}