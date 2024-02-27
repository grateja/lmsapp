package com.vag.lmsapp.app.joborders.create.packages

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class MenuJobOrderPackage(
    @ColumnInfo(name = "id")
    val packageRefId: UUID,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "delivery_id")
    val deliveryId: UUID?,

    @ColumnInfo(name = "discount_in_peso")
    val discountInPeso: Float?,

    var quantity: Int = 1,

    @ColumnInfo(name = "total_price")
    var totalPrice: Float?,

    @ColumnInfo(name = "deleted_at")
    var deletedAt: Instant? = null
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected = false

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