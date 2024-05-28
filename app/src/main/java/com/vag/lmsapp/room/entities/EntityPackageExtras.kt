package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.PACKAGE_EXTRAS
import java.time.Instant
import java.util.*

@Entity(
    tableName = PACKAGE_EXTRAS,
    foreignKeys = [
        ForeignKey(entity = EntityPackage::class, parentColumns = ["id"], childColumns = ["package_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityPackageExtras(
    @Json(name = "package_id")
    @ColumnInfo(name = "package_id")
    val packageId: UUID,

    @Json(name = "extras_id")
    @ColumnInfo(name = "extras_id")
    val extrasId: UUID,

    val quantity: Int,

    @PrimaryKey(autoGenerate = false)
    val id: UUID = UUID.randomUUID(),

    @Json(name = "is_deleted")
    var deleted: Boolean = false
)