package com.vag.lmsapp.app.packages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*

@Parcelize
data class PackageItem(
    val serviceId: UUID,
    val name: String,
    val price: Float,
    val quantity: Float,
    var deletedAt: Instant?,
) : Parcelable