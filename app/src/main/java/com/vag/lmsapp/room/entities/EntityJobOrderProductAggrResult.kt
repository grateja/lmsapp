package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.model.EnumProductType

data class EntityJobOrderProductAggrResult(
    @ColumnInfo(name = "product_name")
    val name: String?,
    val count: Int,

    @ColumnInfo(name = "unit_per_serve")
    val unitPerServe: Float,

    @ColumnInfo(name = "product_type")
    val productType: EnumProductType?,

    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit?,
){
    fun quantityStr() : String {
        return "($unitPerServe $measureUnit) * $count"
    }
}
