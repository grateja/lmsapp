package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import java.util.TimeZone

@Entity(tableName = "shops")
class EntityShop : BaseEntity() {
    var timezone: String = TimeZone.getDefault().id

    var name: String? = null

    var address: String? = null

    @Json(name = "contact_number")
    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null

    @Json(ignore = true)
    var sync: Boolean = false
}