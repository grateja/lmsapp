package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.squareup.moshi.Json

data class EntityJobOrderPaymentFull(
    @Embedded
    val payment: EntityJobOrderPayment,

    @Json(name = "staff")
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id",
        entity = EntityUser::class
    )
    val user: EntityUser,

    @Json(ignore = true)
    @Relation(
        parentColumn = "id",
        entityColumn = "payment_id",
        entity = EntityJobOrder::class
    )
    val jobOrders: List<EntityJobOrder> = emptyList(),
)