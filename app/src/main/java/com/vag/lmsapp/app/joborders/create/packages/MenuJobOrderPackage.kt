package com.vag.lmsapp.app.joborders.create.packages

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MenuJobOrderPackage(
    var joPackageItemId: UUID?,

    @ColumnInfo(name = "id")
    val packageRefId: UUID,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "description")
    val description: String?,

    var quantity: Int = 1,

    @ColumnInfo(name = "total_price")
    var totalPrice: Float,

    @ColumnInfo("void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected = false

    fun quantityStrAbbr() : String {
        return "($quantity * P$totalPrice)"
    }

    fun quantityStr() : String {
        return quantity.toString() + if(quantity == 1) {
            " Package"
        } else if(quantity == 0) {
            ""
        } else {
            " Packages"
        }
    }
}