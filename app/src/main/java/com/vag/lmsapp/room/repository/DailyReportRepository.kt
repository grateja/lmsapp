package com.vag.lmsapp.room.repository

import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.dao.DaoDailyReport
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyReportRepository

@Inject
constructor(
    private val dao: DaoDailyReport
) {
    fun jobOrder(date: LocalDate) = dao.jobOrder(date)

    fun jobOrderPayment(date: LocalDate) = dao.jobOrderPayment(date)

    fun jobOrderPaymentSummary(date: LocalDate) = dao.jobOrderPaymentSummary(date)

    fun jobOrderServiceItems(date: LocalDate, serviceType: EnumServiceType) = dao.jobOrderItemServices(date, serviceType)

    fun jobOrderExtrasItems(date: LocalDate) = dao.jobOrderItemExtraServices(date)

    fun jobOrderProductsItems(date: LocalDate) = dao.jobOrderItemProducts(date)

    fun jobOrderDeliveryItems(date: LocalDate) = dao.jobOrderItemDelivery(date)

    fun expenses(date: LocalDate) = dao.expenses(date)

    fun machineUsageSummary(date: LocalDate) = dao.machineUsageSummary(date)

    fun unpaidJobOrders() = dao.unpaidJobOrders()

    suspend fun getJobOrderItemsDetailsServices(date: LocalDate, serviceType: EnumServiceType) = dao.getJobOrderItemDetailsServices(date, serviceType)
    suspend fun getJobOrderItemsDetailsExtras(date: LocalDate) = dao.getJobOrderItemDetailsExtras(date)
    suspend fun getJobOrderItemsDetailsProducts(date: LocalDate) = dao.getJobOrderItemDetailsProducts(date)
    suspend fun getJobOrderItemsDetailsDeliveries(date: LocalDate) = dao.getJobOrderItemDetailsDeliveries(date)
}