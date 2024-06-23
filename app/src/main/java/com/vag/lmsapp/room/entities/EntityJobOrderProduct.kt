package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_PRODUCTS
import java.util.*

@Entity(tableName = JOB_ORDER_PRODUCTS)
data class EntityJobOrderProduct(
    @Json(name = "job_order_id")
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @Json(name = "product_id")
    @ColumnInfo(name = "product_id")
    var productId: UUID,

    @Json(name = "product_name")
    @ColumnInfo(name = "product_name")
    var productName: String,

    var price: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    @Json(name = "measure_unit")
    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit,

    @Json(name = "unit_per_serve")
    @ColumnInfo(name = "unit_per_serve")
    val unitPerServe: Float,

    var quantity: Int,

    @Json(name = "product_type")
    @ColumnInfo(name = "product_type")
    var productType: EnumProductType,

    @Json(name = "void")
    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)
