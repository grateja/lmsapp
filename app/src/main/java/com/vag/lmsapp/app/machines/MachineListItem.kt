package com.vag.lmsapp.app.machines

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.entities.EntityJobOrderService
import com.vag.lmsapp.room.entities.EntityMachine
import com.vag.lmsapp.room.entities.EntityUser

data class MachineListItem(
    @Embedded
    val machine: EntityMachine,

    @Relation(
        parentColumn = "customer_id",
        entityColumn = "id"
    )
    val customer: EntityCustomer?,

    @Relation(
        parentColumn = "jo_service_id",
        entityColumn = "id"
    )
    val service: EntityJobOrderService?,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: EntityUser?,

    @ColumnInfo(name = "usage_for_the_day")
    val usageForTheDay: Int,

    @ColumnInfo(name = "total_usage")
    val totalUsage: Int
)