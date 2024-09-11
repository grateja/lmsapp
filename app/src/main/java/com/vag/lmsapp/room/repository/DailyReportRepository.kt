package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoDailyReport
import com.vag.lmsapp.room.dao.DaoMonthlyReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyReportRepository

@Inject
constructor(
    private val dao: DaoDailyReport
) {
    suspend fun daily(year: Int, monthNumber: String) = dao.daily(year.toString(), monthNumber)
}