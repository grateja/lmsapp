package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumCRUDAction
import java.util.UUID

@Entity(tableName = "activity_logs")
data class EntityActivityLog(
    @ColumnInfo(name = "user_id")
    val userId: UUID,

    val action: EnumCRUDAction,

    val remarks: String
) : BaseEntity()