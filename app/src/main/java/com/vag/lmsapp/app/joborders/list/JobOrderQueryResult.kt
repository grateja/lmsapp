package com.vag.lmsapp.app.joborders.list

import com.vag.lmsapp.util.ResultCount

data class JobOrderQueryResult(
    val items: List<JobOrderListItem>,
    val count: JobOrderResultSummary?,
    val resultCount: ResultCount?
)