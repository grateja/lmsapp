package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumMeasureUnit
import java.util.*

@Entity(tableName = "job_order_products")
data class EntityJobOrderProduct(
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @ColumnInfo(name = "product_id")
    var productId: UUID,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "price")
    var price: Float,

    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit,

    @ColumnInfo(name = "unit_per_serve")
    val unitPerServe: Float,

    @ColumnInfo(name = "quantity")
    var quantity: Float,

    @ColumnInfo(name = "product_type")
    var productType: EnumProductType,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)/* {
    constructor(jobOrderId: UUID?, productId: UUID, productName: String, price: Float, measureUnit: MeasureUnit, unitPerServe: Float, quantity: Int, isPackage: Boolean, productType: EnumProductType)
     : this(jobOrderId, productId, productName, price, measureUnit, unitPerServe, quantity, isPackage, productType, null)
}*/
