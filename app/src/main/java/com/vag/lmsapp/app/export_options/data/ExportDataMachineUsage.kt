package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import java.time.Instant

data class ExportDataMachineUsage(
    @ColumnInfo("activated")
    val activated: Instant,

    @ColumnInfo("machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo("machine_number")
    val machineNumber: Int,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("service_name")
    val serviceName: String,

    @ColumnInfo("svc_minutes")
    val minutes: Int,

    @ColumnInfo("created_at")
    val createdAt: Instant,
) {
    fun label() : String {
        return "$machineType $machineNumber"
    }
}
