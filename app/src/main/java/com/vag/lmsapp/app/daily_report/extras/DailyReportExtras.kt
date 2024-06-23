package com.vag.lmsapp.app.daily_report.extras

import androidx.room.ColumnInfo

data class DailyReportExtras(
    @ColumnInfo("extras_name")
    val name: String,
    val count: Int,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double
) {
    override fun toString(): String {
        return "($count) $name"
    }
}
