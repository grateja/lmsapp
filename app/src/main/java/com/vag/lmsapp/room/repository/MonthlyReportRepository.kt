package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoMonthlyReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyReportRepository

@Inject
constructor(
    private val dao: DaoMonthlyReport
) {
    suspend fun monthly(year: Int) = dao.monthly(year.toString())
}