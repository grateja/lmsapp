package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class InventoryLogSyncIds(
    @Json(name = "inventory_log_ids")
    val inventoryLogIds: List<UUID>,

    @Json(name = "product_ids")
    val productIds: List<UUID>,

    @Json(name = "staff_ids")
    val staffIds: List<UUID>,

    @Json(name = "expense_ids")
    val expenseIds: List<UUID>,
)
