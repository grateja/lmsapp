package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.INVENTORY_LOGS
import java.util.*

@Entity(tableName = INVENTORY_LOGS)
data class EntityInventoryLog(
    @Json(name = "product_id")
    @ColumnInfo(name = "product_id")
    var productId: UUID? = null,

    @Json(name = "receipt_number")
    @ColumnInfo(name = "receipt_number")
    var receiptNumber: String? = null,

    var quantity: Float = 0f,

    @Json(name = "total_amount")
    @ColumnInfo(name = "total_amount")
    var totalAmount: Float = 0f,

    @Json(name = "staff_id")
    @ColumnInfo(name = "user_id")
    var userId: UUID? = null,

    var remarks: String? = null,

    @Json(name = "expense_id")
    @ColumnInfo(name = "expense_id")
    var expenseId: UUID? = null
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false
}
