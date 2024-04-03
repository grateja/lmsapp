package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.model.EnumPaymentMethod
import java.util.*

@Entity(
    tableName = "job_order_payments",
//    foreignKeys = [
//        ForeignKey(
//            entity = EntityUser::class,
//            parentColumns = ["id"],
//            childColumns = ["userId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ]
)
data class EntityJobOrderPayment(
    @PrimaryKey(autoGenerate = false)
    override var id: UUID,

    @ColumnInfo(name = "payment_method")
    var paymentMethod: EnumPaymentMethod,

    @ColumnInfo(name = "amount_due")
    var amountDue: Float = 0f,

    @ColumnInfo(name = "cash_received")
    var cashReceived: Float,

    @ColumnInfo(name = "userId")
    var userId: UUID,

    @ColumnInfo(name = "or_number")
    var orNumber: String? = null,

    @Embedded
    var entityCashless: EntityCashless? = null,

    @Embedded
    var jobOrderPaymentVoid: EntityJobOrderVoid? = null,
) : BaseEntity() {
    fun change() : Float {
        return cashReceived - amountDue
    }

    fun method() : String {
        return entityCashless?.provider ?: "CASH"
    }
}