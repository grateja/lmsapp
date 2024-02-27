package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo

data class EntityJobOrderExtrasAggrResult(
    @ColumnInfo(name = "extras_name")
    val name: String?,
    val count: String,

    val category: String?,
)
