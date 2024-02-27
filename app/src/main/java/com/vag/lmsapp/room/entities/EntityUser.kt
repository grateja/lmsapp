package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.Role

@Entity(tableName = "users")
class EntityUser(
    @ColumnInfo(name = "role")
    var role: Role,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "permissions")
    var permissions: List<EnumActionPermission>,

    @ColumnInfo(name = "contact_number")
    var contactNumber: String? = null,

    @ColumnInfo(name = "pattern_ids")
    var patternIds: ArrayList<Int>,
) : BaseEntity()
