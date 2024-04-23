package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_EXTRAS
import java.util.*

@Entity(tableName = JOB_ORDER_EXTRAS)
data class EntityJobOrderExtras(
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @ColumnInfo(name = "extras_id")
    var extrasId: UUID,

    @ColumnInfo(name = "extras_name")
    var extrasName: String,

    @ColumnInfo(name = "price")
    var price: Float,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "category")
    var category: String?,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)/* {
    constructor(jobOrderId: UUID?, extrasId: UUID, extrasName: String, price: Float, quantity: Int, isPackage: Boolean, category: String?)
     : this(jobOrderId, extrasId, extrasName, price, quantity, isPackage, category, null)
}*/
