package com.vag.lmsapp.app.lms_live.register

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ShopLinkQrCode(
    val userId: UUID,
    val token: String,
    val tokenId: UUID
) : Parcelable
