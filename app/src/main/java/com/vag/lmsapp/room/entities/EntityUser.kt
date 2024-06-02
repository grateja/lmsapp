package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.util.DbColumns.Companion.USERS

@Entity(tableName = USERS)
class EntityUser : BaseEntity() {
    var role: Role = Role.STAFF

    var name: String = ""

    var email: String = ""

    @Json(ignore = true)
    var password: String = ""

    var permissions: List<EnumActionPermission> = listOf(EnumActionPermission.BASIC)

    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null

    @Json(ignore = true)
    @ColumnInfo(name = "pattern_ids")
    var patternIds: ArrayList<Int> = arrayListOf()

    @Json(ignore = true)
    var sync: Boolean = false
}