package com.vag.lmsapp.app.joborders.create.delivery

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.EnumDeliveryVehicle
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DeliveryCharge(
    var deliveryProfileId: UUID,
    var vehicle: EnumDeliveryVehicle,
    var distance: Float,
    var deliveryOption: EnumDeliveryOption = EnumDeliveryOption.PICKUP_AND_DELIVERY,
    var price: Float,

    @ColumnInfo("discounted_price")
    var discountedPrice: Float,
    var deleted: Boolean,
    var isVoid: Boolean = false,
) : Parcelable {

//    fun price() : Float {
//        return deliveryOption.charge * ((deliveryProfile.pricePerKm * distance) + deliveryProfile.baseFare)
//    }
}