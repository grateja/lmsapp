package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

abstract class BaseEntity (uuid: UUID? = null) {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    open var id: UUID = uuid ?: UUID.randomUUID()

    @ColumnInfo(name = "created_at")
    var createdAt: Instant = Instant.now()

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant = Instant.now()

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false

    @ColumnInfo(name = "sync")
    var sync: Boolean = false
//
//    @ColumnInfo(name = "deleted_by")
//    var deletedBy: UUID? = null
}
