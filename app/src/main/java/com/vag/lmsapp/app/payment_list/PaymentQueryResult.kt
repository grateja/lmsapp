package com.vag.lmsapp.app.payment_list

import com.vag.lmsapp.room.entities.EntityJobOrderPaymentListItem
import com.vag.lmsapp.room.entities.QueryAggrResult

data class PaymentQueryResult(
    val items: List<EntityJobOrderPaymentListItem>,
    val aggResult: QueryAggrResult?
)
