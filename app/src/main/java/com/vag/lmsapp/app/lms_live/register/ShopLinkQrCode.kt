package com.vag.lmsapp.app.lms_live.register

import java.util.UUID

data class ShopLinkQrCode(
    val userId: UUID,
    val token: String
)
