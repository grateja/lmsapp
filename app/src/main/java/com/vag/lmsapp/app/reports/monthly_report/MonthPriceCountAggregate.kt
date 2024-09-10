package com.vag.lmsapp.app.reports.monthly_report

data class MonthPriceCountAggregate(
    val month: EnumMonths,
    val count: Int,
    val price: Float
)