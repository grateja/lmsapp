package com.vag.lmsapp.app.expenses

import com.vag.lmsapp.util.ResultCount

data class ExpenseQueryResult(
    val items: List<ExpenseItemFull>,
    val count: ResultCount
)