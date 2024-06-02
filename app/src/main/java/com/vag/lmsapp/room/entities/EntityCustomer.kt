package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vag.lmsapp.util.DbColumns.Companion.CUSTOMERS

@Entity(tableName = CUSTOMERS)
data class EntityCustomer(
    var crn: String?,
    var name: String? = null
) : BaseEntity() {

    @Json(name = "contact_number")
    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null

    var address: String? = null

    var email: String? = null

    var remarks: String? = null

    @Json(ignore = true)
    var sync: Boolean = false
}