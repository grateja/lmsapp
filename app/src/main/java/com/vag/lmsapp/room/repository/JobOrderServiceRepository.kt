package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.dao.DaoJobOrderService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderServiceRepository
@Inject
constructor (
    private val dao: DaoJobOrderService,
) {
    fun getDashboard(dateFilter: DateFilter) = dao.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)
}