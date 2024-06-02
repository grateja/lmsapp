package com.vag.lmsapp.room.entities

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDERS
import java.util.*

@Entity(
    tableName = JOB_ORDERS,
    foreignKeys = [
        ForeignKey(entity = EntityJobOrderPayment::class, parentColumns = ["id"], childColumns = ["payment_id"], onDelete = ForeignKey.SET_NULL, onUpdate = ForeignKey.CASCADE)
    ]
)
open class EntityJobOrder(
    @Json(name = "job_order_number")
    @ColumnInfo(name = "job_order_number")
    var jobOrderNumber: String?,

    @Json(name = "customer_id")
    @ColumnInfo(name = "customer_id")
    var customerId: UUID,

    @Json(name = "staff_id")
    @ColumnInfo(name = "user_id")
    var userId: UUID,

    var subtotal: Float,

    @Json(name = "discount_in_peso")
    @ColumnInfo(name = "discount_in_peso")
    var discountInPeso: Float,

    @Json(name = "discounted_amount")
    @ColumnInfo(name = "discounted_amount")
    var discountedAmount: Float,

    @Json(name = "job_order_payment_id")
    @ColumnInfo(name = "payment_id")
    var paymentId: UUID? = null,
) : BaseEntity() {
    @Embedded
    var entityJobOrderVoid: EntityJobOrderVoid? = null

    @Json(ignore = true)
    var sync: Boolean = false
}