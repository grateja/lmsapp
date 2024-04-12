package com.vag.lmsapp.app.joborders.create.discount

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.model.EnumDiscountApplicable
//import com.csi.palabakosys.model.DiscountTypeEnum
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MenuDiscount(
    @ColumnInfo(name = "id")
    val discountRefId: UUID?,

    val name: String,

    val value: Float,

    @ColumnInfo(name = "applicable_to")
    val applicableTo: List<EnumDiscountApplicable>,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean,
) : Parcelable {
    @Ignore
    @IgnoredOnParcel
    var selected: Boolean = false

//    private fun applicableTo() : List<EnumDiscountApplicable> {
//        return EnumDiscountApplicable.fromIds(applicableToIds)
//    }

    fun applicableToStr() : String {
        return "Applicable to: " + applicableTo.joinToString(",") {
            it.value
        }
    }

    private fun computePercentage(amount: Float) : Float {
        return (value / 100) * amount
    }

    fun calculateDiscount(amount: Float, applicable: EnumDiscountApplicable) : Float {
        return if(!applicableTo.any{ it == applicable || it == EnumDiscountApplicable.TOTAL_AMOUNT})
            0f
        else
            computePercentage(amount)
    }

    override fun toString(): String {
        return "$name ($value %)"
    }
}