package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class JobOrderSyncIds(
    @Json(name = "job_order_ids")
    val jobOrderIds: List<UUID>,

    @Json(name = "customer_ids")
    val customerIds: List<UUID>,

    @Json(name = "staff_ids")
    val staffIds: List<UUID>,

    @Json(name = "payment_ids")
    val paymentIds: List<UUID>,

//    @Json(name = "paid_by")
//    val paidBys:  List<UUID>,

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
