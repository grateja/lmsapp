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
    suspend fun jobOrders(year: Int) = dao.jobOrders(year.toString())
    suspend fun customers(year: Int) = dao.customers(year.toString())
    suspend fun expenses(year: Int) = dao.expenses(year.toString())
}