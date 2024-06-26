package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumCRUDAction
import com.vag.lmsapp.util.DbColumns.Companion.ACTIVITY_LOGS
import java.util.UUID

@Entity(tableName = ACTIVITY_LOGS)
data class EntityActivityLog(
    @Json(name = "user_id")
    @ColumnInfo(name = "user_id")
    val userId: UUID,

    val action: EnumCRUDAction,

    val remarks: String
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false
}