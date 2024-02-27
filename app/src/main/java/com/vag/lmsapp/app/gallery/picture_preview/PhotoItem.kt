package com.vag.lmsapp.app.gallery.picture_preview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class PhotoItem(
    val id: UUID,
    val createdAt: Instant,
) : Parcelable