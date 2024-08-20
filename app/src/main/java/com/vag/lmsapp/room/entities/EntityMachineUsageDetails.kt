package com.vag.lmsapp.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
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

    @ColumnInfo("svc_service_type")
    val serviceType: EnumServiceType,

    @ColumnInfo("machine_number")
    val machineNumber: Int,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("price")
    val price: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    @ColumnInfo("activated_by")
    val activatedBy: String?,

    val sync: Boolean
): Parcelable {
    fun machineName() : String {
        return "${machineType.value} ${serviceType.value}er $machineNumber"
    }

    fun serviceLabel() : String {
        return "$serviceName ($minutes mins.)"
    }
}
