package com.vag.lmsapp.app.export_options.data

import androidx.room.ColumnInfo
import java.time.Instant

data class ExportDataNewCustomers(
    val crn: String,
    val name: String,

    @ColumnInfo(name = "contact_number")
    var contactNumber: String?,

    var address: String?,

    var email: String?,

    var remarks: String?,

    @ColumnInfo(name = "total_job_orders")
    var totalJobOrders: Int = 0,

    @ColumnInfo(name = "total_washes")
    var totalWashes: Int = 0,

    @ColumnInfo(name = "total_dries")
    var totalDries: Int = 0,

    @ColumnInfo(name = "first_jo")
    var firstJo: Instant? = null,

    @ColumnInfo(name = "last_jo")
    var lastJo: Instant? = null,
)
