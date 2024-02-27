package com.vag.lmsapp.app.customers.list

data class CustomerQueryResult(
    val result: List<CustomerListItem>,
    val count: Int,
)