package com.vag.lmsapp.room.entities

import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.EXTRAS

@Entity(tableName = EXTRAS)
data class EntityExtras(
    var name: String,
    var price: Float,
    var category: String,
    var hidden: Boolean = false
) : BaseEntity() {
    constructor() : this("", 0f, "")

    @Json(ignore = true)
    var sync: Boolean = false
}