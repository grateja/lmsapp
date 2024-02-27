package com.vag.lmsapp.app.joborders.create.extras

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.UUID

@Parcelize
class MenuExtrasItem(
    var joExtrasItemId: UUID?,

    @ColumnInfo(name = "id")
    val extrasRefId: UUID,

    val name: String,

    val price: Float,

    val category: String?,

    var quantity: Int = 1,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted_at")
    var deletedAt: Instant? = null,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var selected = false

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