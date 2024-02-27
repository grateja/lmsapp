package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "customers")
data class EntityCustomer(
    @ColumnInfo(name = "crn")
    var crn: String?,

    @ColumnInfo(name = "name")
    var name: String? = null
) : BaseEntity() {

    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null

    @ColumnInfo(name = "address")
    var address: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null

    @ColumnInfo(name = "remarks")
    var remarks: String? = null
}