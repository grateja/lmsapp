package com.vag.lmsapp.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class EntityCashless(
    @Json(name = "cashless_provider")
    @ColumnInfo(name = "cashless_provider")
    var provider: String?,

    @Json(name ="cashless_ref_number")
    @ColumnInfo(name ="cashless_ref_number")
    var refNumber: String?,

    @Json(name ="cashless_amount")
    @ColumnInfo(name ="cashless_amount")
    var amount: Float?
): Parcelable {
    override fun toString(): String {
        return "$provider (REF#: $refNumber) P $amount"
    }
}