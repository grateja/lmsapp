package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import java.time.Instant

data class ExportDataJobOrderService(
    @ColumnInfo("created_at")
    val createdAt: Instant,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("service_name")
    val serviceName: String,

    @ColumnInfo("svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo("quantity")
    val quantity: Int,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double,
)