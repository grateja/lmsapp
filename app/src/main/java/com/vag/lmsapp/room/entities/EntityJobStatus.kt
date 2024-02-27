package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo

data class EntityJobStatus(
    @ColumnInfo(name = "wash_count")
    val washCount: Int,

    @ColumnInfo(name = "dry_count")
    val dryCount: Int,

    @ColumnInfo(name = "pending_count")
    val pendingCount: Int,
)
