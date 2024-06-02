package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class MachineUsageSyncIds(
    @Json(name = "machine_usage_id")
    val machineUsageId: UUID,

    @Json(name = "job_order_service_id")
    val jobOrderServiceId: UUID,

    @Json(name = "customer_id")
    val customerId: UUID
)
