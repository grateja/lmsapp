package com.vag.lmsapp.app.joborders.create.extras

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class MenuExtrasItem(
    var joExtrasItemId: UUID?,

    @ColumnInfo(name = "id")
    val extrasRefId: UUID,

    val name: String,

    var price: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    var category: String?,

    var quantity: Int = 1,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected = false

    fun total(): Float {
        return quantity * discountedPrice
    }

    fun quantityStrAbbr() : String {
        return "($quantity * P$discountedPrice)"
    }

    fun quantityStr() : String {
        return quantity.toString() + if(quantity == 1) {
            " Load/Item"
        } else if(quantity == 0) {
            ""
        } else {
            " Loads/Items"
        }
    }
}