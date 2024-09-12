package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoMonthlyReport
import com.vag.lmsapp.room.dao.DaoYearlyReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YearlyReportRepository

@Inject
constructor(
    private val dao: DaoYearlyReport
) {
    fun yearly() = dao.yearly()
}