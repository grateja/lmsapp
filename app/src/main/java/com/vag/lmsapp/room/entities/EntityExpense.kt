package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.UUID

@Entity(tableName = "expenses")
data class EntityExpense(
    var remarks: String? = null,
    var amount: Float? = 0f,
    var tag: String? = null,

    @ColumnInfo(name = "created_by")
    var createdBy: UUID? = null
) : BaseEntity()