package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo

data class EntityCashlessPaymentAggrResult(
    @ColumnInfo(name = "cashless_provider")
    val provider: String?,

    @ColumnInfo(name = "count")
    val count: Int,

    @ColumnInfo(name = "amount")
    val amount: Float,
)