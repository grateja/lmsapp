package com.vag.lmsapp.app.dashboard.data

import androidx.room.ColumnInfo

data class DashboardJobOrders(
    @ColumnInfo(name = "paid_count")
    val paidCount: Int?,

    @ColumnInfo(name = "unpaid_count")
    val unpaidCount: Int?,

//    @ColumnInfo(name = "paid_sum")
//    val paidSum: Float?,

//    @ColumnInfo(name = "unpaid_sum")
//    val unpaidSum: Float?,
)