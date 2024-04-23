package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.EnumDeliveryVehicle
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_DELIVERY_CHARGE
import java.util.*

@Entity(tableName = JOB_ORDER_DELIVERY_CHARGE)
data class EntityJobOrderDeliveryCharge(
    @ColumnInfo(name = "delivery_profile_id")
    val deliveryProfileId: UUID,

    val vehicle: EnumDeliveryVehicle,

    @ColumnInfo(name = "delivery_option")
    val deliveryOption: EnumDeliveryOption,

    val price: Float,

    val distance: Float,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)