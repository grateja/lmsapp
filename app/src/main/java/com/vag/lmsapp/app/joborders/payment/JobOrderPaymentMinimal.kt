package com.vag.lmsapp.app.joborders.payment

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityUser
import java.time.Instant
import java.util.*

data class JobOrderPaymentMinimal(
    val id: UUID,

    @ColumnInfo(name = "customer_id")
    val customerId: UUID,

    @ColumnInfo(name = "job_order_number")
    val jobOrderNumber: String?,

    @ColumnInfo(name = "subtotal")
    val subtotal: Float,

    @ColumnInfo(name = "discount_in_peso")
    val discountInPeso: Float,

    @ColumnInfo(name = "discounted_amount")
    val discountedAmount: Float,

    @ColumnInfo(name = "user_id")
    val userId: UUID,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val preparedBy: EntityUser?
) {
    @Ignore
    var selected: Boolean = true
}