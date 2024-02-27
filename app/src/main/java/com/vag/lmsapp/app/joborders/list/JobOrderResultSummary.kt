package com.vag.lmsapp.app.joborders.list

data class JobOrderResultSummary(
    val paidCount: Int = 0,
    val unpaidCount: Int = 0,
    val totalResultCount: Int = 0,
    val paidSum: Float = 0f,
    val unpaidSum: Float = 0f,
    val totalSum: Float = 0f,
)