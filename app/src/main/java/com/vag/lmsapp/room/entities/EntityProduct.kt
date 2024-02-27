package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumMeasureUnit

@Entity(tableName = "products")
class EntityProduct(
    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "price")
    var price: Float,

    @ColumnInfo(name = "current_stock")
    var currentStock: Int,

    @ColumnInfo(name = "measure_unit")
    var measureUnit: EnumMeasureUnit,

    @ColumnInfo(name = "unit_per_serve")
    var unitPerServe: Float,

    @ColumnInfo(name = "product_type")
    var productType: EnumProductType?
) : BaseEntity() {
    constructor() : this(null, 0f, 0, EnumMeasureUnit.PCS, 0f, EnumProductType.OTHER)

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