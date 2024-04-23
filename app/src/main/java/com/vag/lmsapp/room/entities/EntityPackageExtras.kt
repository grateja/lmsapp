package com.vag.lmsapp.room.entities

import androidx.room.*
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
    @ColumnInfo(name = "package_id")
    val packageId: UUID,

    @ColumnInfo(name = "extras_id")
    val extrasId: UUID,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
)