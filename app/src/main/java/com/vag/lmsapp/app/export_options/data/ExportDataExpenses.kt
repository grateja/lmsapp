package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import java.time.Instant

data class ExportDataExpenses(
    @ColumnInfo("created_at")
    val createdAt: Instant,

    @ColumnInfo("remarks")
    val remarks: String,

    @ColumnInfo("amount")
    val amount: Double,

    @ColumnInfo("tag")
    val tag: String?,

    @ColumnInfo("created_by")
    val createdBy: String,
)
