package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.EXPENSES
import java.util.UUID

@Entity(tableName = EXPENSES)
data class EntityExpense(
    var remarks: String? = null,
    var amount: Float? = 0f,
    var tag: String? = null,

    @Json(name = "staff_id")
    @ColumnInfo(name = "created_by")
    var createdBy: UUID? = null
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false
}