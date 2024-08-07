package com.vag.lmsapp.app.joborders.create.products

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumMeasureUnit
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MenuProductItem(
    var joProductItemId: UUID?,

    @ColumnInfo(name = "id")
    val productRefId: UUID,

    val name: String,

    var price: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    @ColumnInfo(name = "measure_unit")
    val measureUnit: EnumMeasureUnit,

    @ColumnInfo(name = "unit_per_serve")
    val unitPerServe: Float,

    var quantity: Int = 1,

    @ColumnInfo(name = "current_stock")
    var currentStock: Float,

    @ColumnInfo(name = "product_type")
    val productType: EnumProductType,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false,

    var hidden: Boolean = false,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected = false

    @IgnoredOnParcel
    @Ignore
    var errorMessage = ""

    fun serving() : String {

        return if(unitPerServe == 1f) { "" } else {
            "$unitPerServe"
        } + "$measureUnit"
    }

    fun total(): Float {
        return quantity * discountedPrice
    }

    fun quantityStrAbbr() : String {
        return "($quantity * P$discountedPrice)"
    }

    fun quantityStr() : String {
        return "*$quantity " + serving()
    }

    fun currentStockStr() : String {
        return "$currentStock $measureUnit remaining"
    }
}

// ariel sachet
// P15.0 / 1 sachet
// quantity - 5 sachets in the cart

// ariel gallon
// 15.0 / 80 mL
// quantity - 5x 80 mL