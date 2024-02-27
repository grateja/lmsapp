package com.vag.lmsapp.app.joborders.create.delivery

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.model.EnumDeliveryVehicle
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class MenuDeliveryProfile(
    @ColumnInfo(name = "id")
    val deliveryProfileRefId: UUID,

    val vehicle: EnumDeliveryVehicle,

    @ColumnInfo(name = "base_fare")
    val baseFare: Float,

    @ColumnInfo(name = "price_per_km")
    val pricePerKm: Float = 0f,

    @ColumnInfo(name = "min_distance")
    val minDistance: Float = 1f,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected: Boolean = false
}