package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class MachineUsageSyncIds(
    @Json(name = "machine_usage_ids")
    val machineUsageIds: List<UUID>,

    @Json(name = "job_order_service_ids")
    val jobOrderServiceIds: List<UUID>,

    @Json(name = "customer_ids")
    val customerIds: List<UUID>
)
