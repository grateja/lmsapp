package com.vag.lmsapp.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class EntityCustomerQueueService (
    @ColumnInfo(name = "customer_name")
    val customerName: String,

    @ColumnInfo(name = "customer_id")
    val customerId: UUID,

    @ColumnInfo(name = "crn")
    val crn: String,

    @ColumnInfo(name = "address")
    val address: String?,

    @ColumnInfo(name = "svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo(name = "svc_service_type")
    val serviceType: EnumServiceType,

    @ColumnInfo(name = "latest_job_order")
    val latestJobOrderDate: Instant?,

    @ColumnInfo(name = "available")
    val available: Int?
) : Parcelable