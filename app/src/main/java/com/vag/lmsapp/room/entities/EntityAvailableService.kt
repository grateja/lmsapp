package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType
import java.util.*

data class EntityAvailableService(
    val id: UUID?,
    val available: Int,
    val minutes: Int,

    @Json(name = "service_name")
    @ColumnInfo(name = "service_name")
    val serviceName: String?,

    @Json(name = "service_id")
    @ColumnInfo(name = "service_id")
    val serviceId: UUID?,

    @Json(name = "job_order_id")
    @ColumnInfo(name = "job_order_id")
    val jobOrderId: UUID?,

    @Json(name = "wash_type")
    @ColumnInfo(name = "wash_type")
    val washType: EnumWashType?,

    @Json(name = "machine_type")
    @ColumnInfo(name = "machine_type")
    val machineType: EnumMachineType?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    var service: EntityJobOrderService
)