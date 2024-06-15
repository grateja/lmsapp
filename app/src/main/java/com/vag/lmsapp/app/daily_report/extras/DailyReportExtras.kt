package com.vag.lmsapp.app.daily_report.extras

import androidx.room.ColumnInfo

data class DailyReportExtras(
    @ColumnInfo("extras_name")
    val name: String,
    val count: Int
) {
    override fun toString(): String {
        return "($count) $name"
    }
}
