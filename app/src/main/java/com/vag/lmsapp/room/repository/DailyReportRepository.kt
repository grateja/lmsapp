package com.vag.lmsapp.room.repository

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

    fun jobOrderWashItems(date: LocalDate) = dao.jobOrderItemWashServices(date)

    fun jobOrderExtrasItems(date: LocalDate) = dao.jobOrderItemExtraServices(date)

    fun jobOrderProductsItems(date: LocalDate) = dao.jobOrderItemProducts(date)

    fun jobOrderDeliveryItems(date: LocalDate) = dao.jobOrderItemDelivery(date)

    fun jobOrderDryItems(date: LocalDate) = dao.jobOrderItemDryServices(date)

    fun expenses(date: LocalDate) = dao.expenses(date)

    fun machineUsageSummary(date: LocalDate) = dao.machineUsageSummary(date)
    fun unpaidJobOrders() = dao.unpaidJobOrders()
}