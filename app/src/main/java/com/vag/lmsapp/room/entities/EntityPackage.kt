package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.PACKAGES

@Entity(tableName = PACKAGES)
data class EntityPackage(
    @Json(name = "package_name")
    @ColumnInfo(name = "package_name")
    var packageName: String = "",

    var description: String? = null,

    @Json(name = "total_price")
    @ColumnInfo(name = "total_price")
    var totalPrice: Float = 0f,

    var hidden: Boolean = false
) : BaseEntity()