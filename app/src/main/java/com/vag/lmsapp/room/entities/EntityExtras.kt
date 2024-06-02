package com.vag.lmsapp.room.entities

import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.EXTRAS

@Entity(tableName = EXTRAS)
data class EntityExtras(
    var name: String?,
    var price: Float,
    var category: String?
) : BaseEntity() {
    constructor() : this("", 0f, null)

    @Json(ignore = true)
    var sync: Boolean = false
}