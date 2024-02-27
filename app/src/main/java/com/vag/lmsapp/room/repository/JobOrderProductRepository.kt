package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.dao.DaoJobOrderProduct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderProductRepository
@Inject
constructor (
    private val dao: DaoJobOrderProduct,
) {
    fun getDashboard(dateFilter: DateFilter) = dao.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)
}