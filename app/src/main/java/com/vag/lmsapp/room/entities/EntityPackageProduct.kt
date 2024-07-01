package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.model.EnumProductType
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
    @Json(name = "package_id")
    @ColumnInfo(name = "package_id")
    val packageId: UUID,

    @Json(name = "product_id")
    @ColumnInfo(name = "product_id")
    val productId: UUID,

    @Json(name = "product_name")
    @ColumnInfo(name = "product_name")
    var productName: String,

    @Json(name = "measure_unit")
    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit,

    @Json(name = "unit_per_serve")
    @ColumnInfo(name = "unit_per_serve")
    val unitPerServe: Float,

    @Json(name = "unit_price")
    @ColumnInfo(name = "unit_price")
    var unitPrice: Float,

    @Json(name = "product_type")
    @ColumnInfo(name = "product_type")
    var productType: EnumProductType,

    var quantity: Int,

    @PrimaryKey(autoGenerate = false)
    val id: UUID = UUID.randomUUID(),

    @Json(name = "is_deleted")
    var deleted: Boolean = false
) {
    fun description(): String {
        return "$quantity $unitPerServe / $measureUnit"
    }
}