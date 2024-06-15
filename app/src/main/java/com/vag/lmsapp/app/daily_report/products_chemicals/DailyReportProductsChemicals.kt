package com.vag.lmsapp.app.daily_report.products_chemicals

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMeasureUnit

data class DailyReportProductsChemicals(
    @ColumnInfo(name = "product_name")
    var name: String,

    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit,

    var count: Int
) {
    override fun toString(): String {
        return "($count $measureUnit) $name"
    }
}
