package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType
import java.time.Instant
import java.util.*

data class EntityMachineUsageDetails(
    @ColumnInfo("id")
    val usageId: UUID,

    val activated: Instant,

    @ColumnInfo("machine_id")
    val machineId: UUID,

    @ColumnInfo("customer_id")
    val customerId: UUID,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("service_name")
    val serviceName: String,

    @ColumnInfo("svc_minutes")
    val minutes: Int,

    @ColumnInfo("svc_wash_type")
    val washType: EnumWashType?,

    @ColumnInfo("svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo("machine_number")
    val machineNumber: Int,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("price")
    val price: Float,

    val sync: Boolean
) {
    fun machineName () : String {
        return machineType.abbr + " " + machineNumber
    }

    fun serviceLabel() : String {
        return "$serviceName ($minutes mins.)"
    }
}
