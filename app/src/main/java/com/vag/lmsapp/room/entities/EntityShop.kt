package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "shops")
class EntityShop : BaseEntity() {
    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "address")
    var address: String? = null

    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null
}