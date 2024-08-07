package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumPaymentMethod
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_PAYMENTS
import java.util.*

@Entity(
    tableName = JOB_ORDER_PAYMENTS,
)
data class EntityJobOrderPayment(
    @PrimaryKey(autoGenerate = false)
    override var id: UUID,

    @Json(name = "payment_method")
    @ColumnInfo(name = "payment_method")
    var paymentMethod: EnumPaymentMethod,

    @Json(name = "amount_due")
    @ColumnInfo(name = "amount_due")
    var amountDue: Float = 0f,

    @Json(name = "cash_received")
    @ColumnInfo(name = "cash_received")
    var cashReceived: Float,

    @Json(name = "change")
    @ColumnInfo(name = "change")
    var change: Float,

    @Json(name = "staff_id")
    @ColumnInfo(name = "user_id")
    var userId: UUID,

    @Json(name = "or_number")
    @ColumnInfo(name = "or_number")
    var orNumber: String? = null,

    @Embedded
    var entityCashless: EntityCashless? = null,
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false

//    fun change() : Float {
//        val cashlessAmount = entityCashless?.amount ?: 0f
//        return cashReceived.plus(cashlessAmount) - amountDue
//    }

    fun method() : String {
        return entityCashless?.provider ?: "CASH"
    }

    fun excessAmount() : Float {
        return entityCashless?.amount?.minus(amountDue) ?: 0f
    }
}