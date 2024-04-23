package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_SERVICES
import java.util.*

@Entity(tableName = JOB_ORDER_SERVICES)
data class EntityJobOrderService(
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @ColumnInfo(name = "service_id")
    var serviceId: UUID,

    @ColumnInfo(name = "service_name")
    var serviceName: String,

    @ColumnInfo(name = "price")
    var price: Float,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "used")
    var used: Int = 0,

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
