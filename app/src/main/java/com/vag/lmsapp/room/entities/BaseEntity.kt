package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.time.Instant
import java.util.*

abstract class BaseEntity (uuid: UUID? = null) {
    @PrimaryKey(autoGenerate = false)
    open var id: UUID = uuid ?: UUID.randomUUID()

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Instant = Instant.now()

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant = Instant.now()

    @Json(name = "is_deleted")
    var deleted: Boolean = false
//
//    @Json(ignore = true)
//    var sync: Boolean = false
}
