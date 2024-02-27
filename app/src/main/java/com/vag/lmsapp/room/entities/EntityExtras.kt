package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "extras")
data class EntityExtras(
    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "price")
    var price: Float,

    @ColumnInfo(name = "category")
    var category: String?
) : BaseEntity() {
    constructor() : this("", 0f, null)
}