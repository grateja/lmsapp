package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.PACKAGE_SERVICES
import java.time.Instant
import java.util.*

@Entity(
    tableName = PACKAGE_SERVICES,
    foreignKeys = [
        ForeignKey(entity = EntityPackage::class, parentColumns = ["id"], childColumns = ["package_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityPackageService(
    @Json(name = "package_id")
    @ColumnInfo(name = "package_id")
    val packageId: UUID,

    @Json(name = "service_id")
    @ColumnInfo(name = "service_id")
    val serviceId: UUID,

    val quantity: Int,

    @PrimaryKey(autoGenerate = false)
    val id: UUID = UUID.randomUUID(),

    @Json(name = "is_deleted")
    var deleted: Boolean = false
)