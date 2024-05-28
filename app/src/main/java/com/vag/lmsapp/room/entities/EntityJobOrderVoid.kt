package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import java.time.Instant
import java.util.*

class EntityJobOrderVoid(
    @Json(name = "void_by")
    @ColumnInfo(name = "void_by")
    var voidByUserId: UUID?,

    @Json(name = "void_remarks")
    @ColumnInfo(name = "void_remarks")
    var remarks: String?,

    @Json(name = "void_date")
    @ColumnInfo(name = "void_date")
    var date: Instant? = Instant.now(),
)