package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import java.time.Instant
import java.util.*

class EntityJobOrderVoid(
    @ColumnInfo(name = "void_by")
    var voidByUserId: UUID?,

    @ColumnInfo(name = "void_remarks")
    var remarks: String?,

    @ColumnInfo(name = "void_date")
    var date: Instant? = Instant.now(),
)