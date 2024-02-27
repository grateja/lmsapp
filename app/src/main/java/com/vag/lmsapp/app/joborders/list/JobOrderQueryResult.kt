package com.vag.lmsapp.app.joborders.list

data class JobOrderQueryResult(
    val items: List<JobOrderListItem>,
    val count: JobOrderResultSummary?,
)