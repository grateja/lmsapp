package com.vag.lmsapp.app.daily_report.products_chemicals

import androidx.room.ColumnInfo

data class DailyReportProductsChemicalsSummary(
    @ColumnInfo("detergent_count")
    val detergentCount: Int,

    @ColumnInfo("fab_con_count")
    val fabConCount: Int,

    @ColumnInfo("others_count")
    val othersCount: Int,

    @ColumnInfo("total_count")
    val totalCount: Int
)
