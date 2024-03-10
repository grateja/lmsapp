package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.model.EnumDiscountApplicable
import java.util.*

@Entity(tableName = "job_order_discounts")
data class EntityJobOrderDiscount(
    @ColumnInfo(name = "discount_id")
    val discountId: UUID?,

    val name: String,

    val value: Float,

    @ColumnInfo(name = "applicable_to")
    val applicableTo: List<EnumDiscountApplicable>,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id) {
    private fun compute(amount: Float) : Float {
        return (value / 100) * amount
    }

    fun getDiscount(amount: Float, applicable: EnumDiscountApplicable) : Float {
        if(!applicableTo.any{ it == applicable || it == EnumDiscountApplicable.TOTAL_AMOUNT}) return 0f

        return compute(amount)
    }
}