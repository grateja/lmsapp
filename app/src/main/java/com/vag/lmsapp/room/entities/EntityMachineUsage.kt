package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "machine_usages")
class EntityMachineUsage(
    @ColumnInfo(name = "machine_id")
    val machineId: UUID?,

    @ColumnInfo(name = "job_order_service_id")
    val jobOrderServiceId: UUID?,

    @ColumnInfo(name = "customer_id")
    val customerId: UUID?
) : BaseEntity()