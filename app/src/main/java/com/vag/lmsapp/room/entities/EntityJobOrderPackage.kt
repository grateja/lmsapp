package com.vag.lmsapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_PACKAGES
import java.util.UUID

@Entity(JOB_ORDER_PACKAGES)
data class EntityJobOrderPackage(
    @ColumnInfo("job_order_id")
    val jobOrderId: UUID,

    @ColumnInfo("package_id")
    val packageId: UUID,

    @ColumnInfo("package_name")
    val packageName: String,

    val price: Float,

    val quantity: Int,

    @Json(name = "void")
    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    override var id: UUID,
): BaseEntity()
