package com.vag.lmsapp.app.joborders.create.delivery

import android.os.Parcelable
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.EnumDeliveryVehicle
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
class DeliveryCharge(
    var deliveryProfileId: UUID,
    var vehicle: EnumDeliveryVehicle,
    var distance: Float,
    var deliveryOption: EnumDeliveryOption = EnumDeliveryOption.PICKUP_AND_DELIVERY,
    var price: Float,
    var deletedAt: Instant?,
    var isVoid: Boolean = false,
) : Parcelable {

//    fun price() : Float {
//        return deliveryOption.charge * ((deliveryProfile.pricePerKm * distance) + deliveryProfile.baseFare)
//    }
}