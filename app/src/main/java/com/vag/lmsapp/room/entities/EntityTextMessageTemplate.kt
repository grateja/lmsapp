package com.vag.lmsapp.room.entities

import androidx.room.Entity
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.TEXT_MESSAGE_TEMPLATES

@Entity(tableName = TEXT_MESSAGE_TEMPLATES)
data class EntityTextMessageTemplate(
    var title: String,
    var message: String,
) : BaseEntity() {
    @Json(ignore = true)
    var sync: Boolean = false
}