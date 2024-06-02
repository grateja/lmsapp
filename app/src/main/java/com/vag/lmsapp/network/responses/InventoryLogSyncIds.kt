package com.vag.lmsapp.network.responses

import com.squareup.moshi.Json
import java.util.UUID

data class InventoryLogSyncIds(
    @Json(name = "inventory_log_id")
    val inventoryLogId: UUID,

    @Json(name = "product_id")
    val productId: UUID,

    @Json(name = "staff_id")
    val userId: UUID,

    @Json(name = "expense_id")
    val expenseId: UUID,
)
