package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class PaymentSynIds(
    @Json(name = "payment_ids")
    val paymentIds: List<UUID>,

    @Json(name = "job_order_ids")
    val jobOrderIds: List<UUID>,

    @Json(name = "staff_ids")
    val staffIds: List<UUID>
)
