package com.vag.lmsapp.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class EntityCashless(
    @ColumnInfo(name = "cashless_provider")
    var provider: String?,

    @ColumnInfo(name ="cashless_ref_number")
    var refNumber: String?,

    @ColumnInfo(name ="cashless_amount")
    var amount: Float?
): Parcelable {
    override fun toString(): String {
        return "$provider (REF#: $refNumber) P $amount"
    }
}