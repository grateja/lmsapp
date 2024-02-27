package com.vag.lmsapp.app.joborders.create.discount

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.model.EnumDiscountType
import com.vag.lmsapp.model.EnumDiscountApplicable
//import com.csi.palabakosys.model.DiscountTypeEnum
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class MenuDiscount(
    @ColumnInfo(name = "id")
    val discountRefId: UUID?,

    val name: String,

    val value: Float,

    @ColumnInfo(name = "applicable_to")
    val applicableTo: List<EnumDiscountApplicable>,

    @ColumnInfo(name = "discount_type")
    val discountType: EnumDiscountType,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted_at")
    var deletedAt: Instant?,
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
        else if(discountType == EnumDiscountType.PERCENTAGE)
            computePercentage(amount)
        else
            amount
    }

    override fun toString(): String {
        return if(discountType == EnumDiscountType.PERCENTAGE) {
            "$name ($value %)"
        } else {
            "$name (P $value)"
        }
    }
}