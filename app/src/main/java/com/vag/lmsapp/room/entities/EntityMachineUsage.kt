package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.MACHINE_USAGES
import java.util.*

@Entity(tableName = MACHINE_USAGES)
class EntityMachineUsage(
    @Json(name = "machine_id")
    @ColumnInfo(name = "machine_id")
    val machineId: UUID?,

    @Json(name = "job_order_service_id")
    @ColumnInfo(name = "job_order_service_id")
    val jobOrderServiceId: UUID?,

    @Json(name = "customer_id")
    @ColumnInfo(name = "customer_id")
    val customerId: UUID?,

    @Json(name = "user_id")
    @ColumnInfo(name = "user_id")
    val userId: UUID?
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false
}