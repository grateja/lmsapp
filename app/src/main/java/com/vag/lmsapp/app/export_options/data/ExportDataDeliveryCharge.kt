package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.EnumDeliveryVehicle
import java.time.Instant

data class ExportDataDeliveryCharge(
    @ColumnInfo("vehicle")
    val vehicle: EnumDeliveryVehicle,

    @ColumnInfo("delivery_option")
    val deliveryOption: EnumDeliveryOption,

    @ColumnInfo("distance")
    val distance: Double,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("created_at")
    val createdAt: Instant,
)
