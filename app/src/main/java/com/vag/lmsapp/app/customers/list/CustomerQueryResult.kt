package com.vag.lmsapp.app.customers.list

import com.vag.lmsapp.util.ResultCount

data class CustomerQueryResult(
    val result: List<CustomerListItem>,
    val count: ResultCount,
)