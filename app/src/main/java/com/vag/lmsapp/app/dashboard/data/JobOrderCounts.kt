package com.vag.lmsapp.app.dashboard.data

import androidx.room.ColumnInfo

data class JobOrderCounts(
    @ColumnInfo(name = "paid_count")
    val paidCount: Int,

    @ColumnInfo(name = "unpaid_count")
    val unpaidCount: Int
) {
    fun total(): Int {
        return paidCount + unpaidCount
    }
}
