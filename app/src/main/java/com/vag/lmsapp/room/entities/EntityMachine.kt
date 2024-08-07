package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.util.DbColumns.Companion.MACHINES
import java.util.*

@Entity(tableName = MACHINES)
data class EntityMachine(
    @Json(name = "stack_order")
    @ColumnInfo(name = "stack_order")
    var stackOrder: Int?,

    @Json(name = "machine_type")
    @ColumnInfo(name = "machine_type")
    var machineType: EnumMachineType?,

    @Json(name = "service_type")
    @ColumnInfo(name = "service_type")
    var serviceType: EnumServiceType?,

    @Json(name = "ip_end")
    @ColumnInfo(name = "ip_end")
    var ipEnd: Int,

    @Json(name = "machine_number")
    @ColumnInfo(name = "machine_number")
    var machineNumber: Int,

    @Json(ignore = true)
    @Embedded
    var activationRef: EntityActivationRef? = null
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false

    fun machineName() : String {
        return "${machineType?.value} ${serviceType?.value}er $machineNumber"
    }

    @Json(ignore = true)
    @ColumnInfo(name = "service_activation_id")
    var serviceActivationId: UUID? = null
}
