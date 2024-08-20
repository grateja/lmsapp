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

    @Json(name = "service_name")
    @ColumnInfo(name = "service_name")
    var serviceName: String,

    @Embedded
    var serviceRef: EntityServiceRef,

//    @ColumnInfo(name = "unit_price")
    @Json(name = "unit_price")
    var unitPrice: Float,

    var quantity: Int,

    @PrimaryKey(autoGenerate = false)
    val id: UUID = UUID.randomUUID(),

    @Json(name = "is_deleted")
    var deleted: Boolean = false
) {
    fun label(): String {
        return "${serviceRef.machineType.abbr} ${serviceName}"
    }
}