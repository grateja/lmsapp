package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.dao.DaoJobOrderPickupDelivery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderPickupDeliveryRepository
@Inject
constructor (
    private val dao: DaoJobOrderPickupDelivery,
) {
    fun getDashboard(dateFilter: DateFilter) = dao.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)
}