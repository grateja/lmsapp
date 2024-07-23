package com.vag.lmsapp.app.joborders.create.products

import java.util.UUID

data class ProductAvailabilityChecker(
    val productId: UUID,
    val joProductItemId: UUID?,
    val quantity: Int
)
