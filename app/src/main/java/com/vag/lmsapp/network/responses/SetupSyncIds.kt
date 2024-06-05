package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class SetupSyncIds(
    @Json(name = "shop_id")
    val shopId: UUID,
    @Json(name = "staffs_ids")
    val staffsIds: List<UUID>?,

    @Json(name = "services_ids")
    val servicesIds: List<UUID>?,

    @Json(name = "products_ids")
    val productsIds: List<UUID>?,

    @Json(name = "extras_ids")
    val extrasIds: List<UUID>?,

    @Json(name = "delivery_profiles_ids")
    val deliveryProfilesIds: List<UUID>?,

    @Json(name = "discounts_ids")
    val discountsIds: List<UUID>?,

    @Json(name = "machines_ids")
    val machineIds: List<UUID>?
)
