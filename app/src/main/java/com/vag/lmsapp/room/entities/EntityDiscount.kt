package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumDiscountApplicable
import com.vag.lmsapp.util.DbColumns.Companion.DISCOUNTS

@Entity(tableName = DISCOUNTS)
class EntityDiscount(
    var name: String? = null,

    var value: Float,

    @ColumnInfo(name = "applicable_to")
    var applicableTo: List<EnumDiscountApplicable>
) : BaseEntity(null) {
    constructor(applicableTo: List<EnumDiscountApplicable>) : this(null, 0f, applicableTo)

    fun applicableToStr() : String {
        return "Applicable to: " + applicableTo.joinToString(",") {
            it.value
        }
    }
    override fun toString(): String {
        return "$name ($value %)"
    }
}