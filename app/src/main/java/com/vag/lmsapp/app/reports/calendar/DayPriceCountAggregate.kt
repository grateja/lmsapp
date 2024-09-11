package com.vag.lmsapp.app.reports.calendar

import java.time.LocalDate

data class DayPriceCountAggregate(
    val date: LocalDate,
    val count: Int,
    val price: Float
)