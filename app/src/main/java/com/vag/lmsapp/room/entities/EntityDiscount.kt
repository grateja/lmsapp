package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumDiscountType
import com.vag.lmsapp.model.EnumDiscountApplicable
//import com.csi.palabakosys.model.DiscountTypeEnum

@Entity(tableName = "discounts")
class EntityDiscount(
    var name: String? = null,

    var value: Float,

    @ColumnInfo(name = "discount_type")
    var discountType: EnumDiscountType?,

    @ColumnInfo(name = "applicable_to")
    var applicableTo: List<EnumDiscountApplicable>
) : BaseEntity(null) {
    constructor(discountType: EnumDiscountType, applicableTo: List<EnumDiscountApplicable>) : this(null, 0f, discountType, applicableTo)

    fun applicableToStr() : String {
        return "Applicable to: " + applicableTo.joinToString(",") {
            it.value
        }
    }
    override fun toString(): String {
        return if(discountType == EnumDiscountType.PERCENTAGE) {
            "$name ($value %)"
        } else {
            "$name (P $value)"
        }
    }
}