package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EntityMachineUsageFull(
    @Embedded
    val machineUsage: EntityMachineUsage,

    @Relation(
        parentColumn = "customer_id",
        entityColumn = "id"
    )
    val customer: EntityCustomer,

    @Relation(
        parentColumn = "job_order_service_id",
        entityColumn = "id"
    )
    val jobOrderService: EntityJobOrderService
)
