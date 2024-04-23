package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.util.DbColumns.Companion.INVENTORY_LOGS
import java.util.*

@Entity(tableName = INVENTORY_LOGS)
data class EntityInventoryLog(
    @ColumnInfo(name = "product_id")
    var productId: UUID? = null,

    @ColumnInfo(name = "receipt_number")
    var receiptNumber: String? = null,

    var quantity: Float = 0f,

    @ColumnInfo(name = "total_amount")
    var totalAmount: Float = 0f,

    @ColumnInfo(name = "user_id")
    var userId: UUID? = null,

    var remarks: String? = null,

    @ColumnInfo(name = "expense_id")
    var expenseId: UUID? = null
) : BaseEntity()
