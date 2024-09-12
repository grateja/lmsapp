package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoCalendarReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyReportRepository

@Inject
constructor(
    private val dao: DaoCalendarReport
) {
    suspend fun daily(year: Int, monthNumber: String) = dao.daily(year.toString(), monthNumber)
}