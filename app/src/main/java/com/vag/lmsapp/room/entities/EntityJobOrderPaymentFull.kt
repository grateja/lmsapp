package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EntityJobOrderPaymentFull(
    @Embedded
    val payment: EntityJobOrderPayment,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id",
        entity = EntityUser::class
    )
    val user: EntityUser,

    @Relation(
        parentColumn = "id",
        entityColumn = "payment_id",
        entity = EntityJobOrder::class
    )
    val jobOrders: List<EntityJobOrder>,
)