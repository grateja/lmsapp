package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.MACHINE_REMARKS
import java.util.*

@Entity(tableName = MACHINE_REMARKS)
class EntityMachineRemarks(
    @Json(name = "machine_id")
    @ColumnInfo(name = "machine_id")
    var machineId: UUID,

    @Json(name = "staff_id")
    @ColumnInfo(name = "user_id")
    var userId: UUID?,

    var title: String? = null,

    var remarks: String? = null,

    @Json(ignore = true)
    var sync: Boolean = false
) : BaseEntity()