package com.vag.lmsapp.app.daily_report.wash_dry_services

import androidx.room.ColumnInfo

data class DailyReportWashDrySummary(
    @ColumnInfo("wash_count")
    val washCount: Int,

    @ColumnInfo("dry_count")
    val dryCount: Int,

    @ColumnInfo("total_count")
    val totalCount: Int,
)