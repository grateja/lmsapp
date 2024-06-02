package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class JobOrderSyncIds(
    @Json(name = "job_order_id")
    val jobOrderId: UUID,

    @Json(name = "customer_id")
    val customerId: UUID,

    @Json(name = "created_by")
    val createdBy: UUID,

    @Json(name = "payment_id")
    val paymentId: UUID?,

    @Json(name = "paid_by")
    val paidBy: UUID?,

//    @Json(name = "services_ids")
//    val servicesIds: List<UUID>?,
//
//    @Json(name = "products_ids")
//    val productsIds: List<UUID>?,
//
//    @Json(name = "extras_ids")
//    val extrasIds: List<UUID>?,
//
//    @Json(name = "delivery_charge_id")
//    val deliveryChargeId: UUID?,
//
//    @Json(name = "discount_id")
//    val discountId: UUID?
)
