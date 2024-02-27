package com.vag.lmsapp.app.customers.list

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.vag.lmsapp.room.entities.EntityCustomer
import java.time.Instant

data class CustomerListItem(
    @Embedded
    val customer: EntityCustomer,

    @ColumnInfo(name = "last_visit")
    val lastVisit: Instant?,

    @ColumnInfo(name = "first_visit")
    val firstVisit: Instant?,

    @ColumnInfo(name = "total_jo")
    val totalJo: Int?,

    @ColumnInfo(name = "paid_count")
    val paidJo: Int?,
) {
    fun jobOrdersCountStr() : String {
        return "$paidJo / $totalJo Paid Job Orders"
    }

    fun paidAll() : Boolean {
        return totalJo != null && totalJo > 0 && totalJo == paidJo
    }
}