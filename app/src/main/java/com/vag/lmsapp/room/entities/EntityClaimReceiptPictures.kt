package com.vag.lmsapp.room.entities

import androidx.room.*
import com.vag.lmsapp.util.DbColumns.Companion.CLAIM_RECEIPT_PICTURES
import java.time.Instant
import java.util.*

@Entity(
    tableName = CLAIM_RECEIPT_PICTURES,
    foreignKeys = [
        ForeignKey(entity = EntityJobOrder::class, parentColumns = ["id"], childColumns = ["job_order_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityClaimReceiptPictures(
    @ColumnInfo(name = "job_order_id")
    val jobOrderId: UUID,

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