package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_SERVICES
import java.util.*

@Entity(tableName = JOB_ORDER_SERVICES)
data class EntityJobOrderService(
    @Json(name = "job_order_id")
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @Json(name = "service_id")
    @ColumnInfo(name = "service_id")
    var serviceId: UUID,

    @Json(name = "service_name")
    @ColumnInfo(name = "service_name")
    var serviceName: String,

    var price: Float,

    var quantity: Int,

    var used: Int = 0,

    @Json(name = "void")
    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @Embedded
    var serviceRef: EntityServiceRef,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id) {
    fun label() : String {
        return "${serviceRef.machineType.abbr} $serviceName (${serviceRef.minutes} mins.)"
    }
}
