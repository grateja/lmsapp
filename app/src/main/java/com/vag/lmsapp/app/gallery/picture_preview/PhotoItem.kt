package com.vag.lmsapp.app.gallery.picture_preview

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class PhotoItem(
    val id: UUID,

    @ColumnInfo("created_at")
    val createdAt: Instant,
) : Parcelable {
    @Ignore
    @IgnoredOnParcel
    var fileDeleted: Boolean = false
}