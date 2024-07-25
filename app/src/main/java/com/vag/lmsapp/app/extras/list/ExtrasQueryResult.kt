package com.vag.lmsapp.app.extras.list

import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.util.ResultCount

data class ExtrasQueryResult(
    val items: List<ExtrasItemFull>,
    val resultCount: ResultCount?
)
