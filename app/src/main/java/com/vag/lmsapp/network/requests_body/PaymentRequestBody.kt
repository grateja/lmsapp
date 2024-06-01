package com.vag.lmsapp.network.requests_body

import com.vag.lmsapp.room.entities.EntityJobOrderPayment
import com.vag.lmsapp.room.entities.EntityUser
import java.util.UUID

data class PaymentRequestBody(
    val payment: EntityJobOrderPayment,
    val staff: EntityUser,
    val jobOrderIds: List<UUID>
)
