package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.EnumDeliveryVehicle
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_DELIVERY_CHARGE
import java.util.*

@Entity(tableName = JOB_ORDER_DELIVERY_CHARGE)
data class EntityJobOrderDeliveryCharge(
    @Json(name = "delivery_profile_id")
    @ColumnInfo(name = "delivery_profile_id")
    val deliveryProfileId: UUID,

    val vehicle: EnumDeliveryVehicle,

    @Json(name = "delivery_option")
    @ColumnInfo(name = "delivery_option")
    val deliveryOption: EnumDeliveryOption,

    val price: Float,

    val distance: Float,

    @Json(name = "void")
    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)