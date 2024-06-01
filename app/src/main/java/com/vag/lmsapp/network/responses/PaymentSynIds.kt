package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class PaymentSynIds(
    @Json(name = "payment_id")
    val paymentId: UUID,

    @Json(name = "job_orders_ids")
    val jobOrderIds: List<UUID>,

    @Json(name = "paid_by")
    val paidBy: UUID
)
