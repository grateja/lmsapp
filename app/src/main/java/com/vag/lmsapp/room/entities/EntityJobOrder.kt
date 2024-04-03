package com.vag.lmsapp.room.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = "job_orders",
    foreignKeys = [
        ForeignKey(entity = EntityJobOrderPayment::class, parentColumns = ["id"], childColumns = ["payment_id"], onDelete = ForeignKey.SET_NULL, onUpdate = ForeignKey.CASCADE)
    ]
)
open class EntityJobOrder(
    @ColumnInfo(name = "job_order_number")
    var jobOrderNumber: String?,

    @ColumnInfo(name = "customer_id")
    var customerId: UUID,

    @ColumnInfo(name = "user_id")
    var userId: UUID,

    @ColumnInfo(name = "subtotal")
    var subtotal: Float,

    @ColumnInfo(name = "discount_in_peso")
    var discountInPeso: Float,

    @ColumnInfo(name = "discounted_amount")
    var discountedAmount: Float,

    @ColumnInfo(name = "payment_id")
    var paymentId: UUID? = null,
) : BaseEntity() {
    @Embedded
    var entityJobOrderVoid: EntityJobOrderVoid? = null
}