package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.util.DbColumns.Companion.PACKAGE_PRODUCTS
import java.time.Instant
import java.util.*

@Entity(
    tableName = PACKAGE_PRODUCTS,
    foreignKeys = [
        ForeignKey(entity = EntityPackage::class, parentColumns = ["id"], childColumns = ["package_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityPackageProduct(
    @ColumnInfo(name = "package_id")
    val packageId: UUID,

    @ColumnInfo(name = "product_id")
    val productId: UUID,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
)