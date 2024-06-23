package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.model.EnumProductType
import java.time.Instant

data class ExportDataJobOrderProduct(
    @ColumnInfo("created_at")
    val createdAt: Instant,

    @ColumnInfo("job_order_number")
    val jobOrderNumber: String,

    @ColumnInfo("customer_name")
    val customerName: String,

    @ColumnInfo("product_name")
    val productName: String,

    @ColumnInfo("product_type")
    val productType: EnumProductType,

    @ColumnInfo("measure_unit")
    val measureUnit: EnumMeasureUnit,

    @ColumnInfo("quantity")
    val quantity: Int,

    @ColumnInfo("discounted_price")
    val discountedPrice: Double,
)