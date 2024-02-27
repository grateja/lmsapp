package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.dao.DaoJobOrderExtras
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderExtrasRepository
@Inject
constructor (
    private val dao: DaoJobOrderExtras,
) {
    fun getDashboard(dateFilter: DateFilter) = dao.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)
}