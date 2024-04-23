package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vag.lmsapp.util.DbColumns.Companion.PACKAGES

@Entity(tableName = PACKAGES)
data class EntityPackage(
    @ColumnInfo(name = "package_name")
    var packageName: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "total_price")
    val totalPrice: Float?,

//    @ColumnInfo(name = "delivery_id")
//    val deliveryId: UUID?,
) : BaseEntity() {
    constructor() : this("", "", null)
}