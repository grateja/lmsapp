package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import java.time.Instant
import java.util.*

class EntityJobOrderListItem (
    jobOrderNumber: String?,
    customerId: UUID,
    customerName: String,
    preparedBy: UUID,
) : EntityJobOrder(jobOrderNumber, customerId, preparedBy, 0f, 0f, 0f) {
    @ColumnInfo(name = "total_amount")
    var totalAmount: Float = 0f

    @ColumnInfo(name = "date_paid")
    var datePaid: Instant? = null

    var balance: Float = 0f
}