package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.util.DbColumns.Companion.PRODUCTS

@Entity(tableName = PRODUCTS)
class EntityProduct(
    var name: String,

    var price: Float,

    @Json(name = "current_stock")
    @ColumnInfo(name = "current_stock")
    var currentStock: Float,

    @Json(name = "measure_unit")
    @ColumnInfo(name = "measure_unit")
    var measureUnit: EnumMeasureUnit,

    @Json(name = "unit_per_serve")
    @ColumnInfo(name = "unit_per_serve")
    var unitPerServe: Float,

    @Json(name = "product_type")
    @ColumnInfo(name = "product_type")
    var productType: EnumProductType
) : BaseEntity() {
    constructor() : this("", 0f, 0f, EnumMeasureUnit.PCS, 0f, EnumProductType.OTHER)

    @Json(ignore = true)
    var sync: Boolean = false

    fun currentStockStr() : String {
        return "$currentStock $measureUnit remaining"
    }

//    @ColumnInfo(name = "name")
//    var name: String? = null
//
//    @ColumnInfo(name = "price")
//    var price: Float = 0f
//
//    @ColumnInfo(name = "current_stock")
//    var currentStock: Int = 0
//
//    @ColumnInfo(name = "product_type")
//    var productType: ProductType? = null
}