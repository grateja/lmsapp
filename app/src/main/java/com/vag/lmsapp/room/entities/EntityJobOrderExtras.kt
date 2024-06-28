package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_EXTRAS
import java.util.*

@Entity(tableName = JOB_ORDER_EXTRAS)
data class EntityJobOrderExtras(
    @Json(name = "job_order_id")
    @ColumnInfo(name = "job_order_id")
    var jobOrderId: UUID?,

    @Json(name = "extras_id")
    @ColumnInfo(name = "extras_id")
    var extrasId: UUID,

    @Json(name = "job_order_package_id")
    @ColumnInfo(name = "job_order_package_id")
    var jobOrderPackageId: UUID?,

    @Json(name = "extras_name")
    @ColumnInfo(name = "extras_name")
    var extrasName: String,

    var price: Float,

    @ColumnInfo("discounted_price")
    val discountedPrice: Float,

    var quantity: Int,

    var category: String?,

    @Json(name = "void")
    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
) : BaseEntity(id)/* {
    constructor(jobOrderId: UUID?, extrasId: UUID, extrasName: String, price: Float, quantity: Int, isPackage: Boolean, category: String?)
     : this(jobOrderId, extrasId, extrasName, price, quantity, isPackage, category, null)
}*/
