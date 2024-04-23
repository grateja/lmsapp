package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.util.DbColumns.Companion.JOB_ORDER_PICTURES
import java.time.Instant
import java.util.*

@Entity(
    tableName = JOB_ORDER_PICTURES,
    foreignKeys = [
        ForeignKey(entity = EntityJobOrder::class, parentColumns = ["id"], childColumns = ["job_order_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ]
)
data class EntityJobOrderPictures(
    @ColumnInfo(name = "job_order_id")
    val jobOrderId: UUID,

//    @ColumnInfo(name = "file_name")
//    val fileName: String,

//    @ColumnInfo(name = "uri_id")
//    val uriId: Long,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
) {
    @Ignore
    var fileDeleted = false
}