package com.vag.lmsapp.app.lms_live.sync

data class SyncCounts(
    val jobOrderCount: Int,
    val machineUsageCount: Int,
    val inventoryLogCount: Int,
    val paymentCount: Int,
    val expensesCount: Int,
    val customerCount: Int
)