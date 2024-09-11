package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.reports.summary_report.PriceCountAggregate
import com.vag.lmsapp.app.reports.summary_report.expenses.SummaryReportExpenses
import com.vag.lmsapp.app.reports.summary_report.job_order.SummaryReportJobOrder
import com.vag.lmsapp.app.reports.summary_report.job_order_items.SummaryReportJobOrderItemDetails
import com.vag.lmsapp.app.reports.summary_report.job_order_paid.SummaryReportJobOrderPayment
import com.vag.lmsapp.app.reports.summary_report.job_order_paid.SummaryReportJobOrderPaymentSummary
import com.vag.lmsapp.app.reports.summary_report.machine_usage.SummaryReportMachineUsageSummary
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.dao.DaoSummaryReport
import com.vag.lmsapp.util.DateFilter
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryReportRepository

@Inject
constructor(
    private val dao: DaoSummaryReport
) {
    fun jobOrder(dateFilter: DateFilter): LiveData<SummaryReportJobOrder> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrder(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderPayment(dateFilter: DateFilter): LiveData<List<SummaryReportJobOrderPayment>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderPayment(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderPaymentSummary(dateFilter: DateFilter): LiveData<SummaryReportJobOrderPaymentSummary> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderPaymentSummary(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderServiceItems(dateFilter: DateFilter, serviceType: EnumServiceType): LiveData<PriceCountAggregate> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderItemServices(dateFilter.dateFrom, dateTo, serviceType)
    }

    fun jobOrderExtrasItems(dateFilter: DateFilter): LiveData<PriceCountAggregate> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderItemExtraServices(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderProductsItems(dateFilter: DateFilter): LiveData<PriceCountAggregate> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderItemProducts(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderDeliveryItems(dateFilter: DateFilter): LiveData<PriceCountAggregate> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderItemDelivery(dateFilter.dateFrom, dateTo)
    }

    fun expenses(dateFilter: DateFilter): LiveData<List<SummaryReportExpenses>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.expenses(dateFilter.dateFrom, dateTo)
    }

    fun machineUsageSummary(dateFilter: DateFilter): LiveData<List<SummaryReportMachineUsageSummary>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.machineUsageSummary(dateFilter.dateFrom, dateTo)
    }

    fun unpaidJobOrders() = dao.unpaidJobOrders()

    suspend fun getJobOrderItemsDetailsServices(dateFilter: DateFilter, serviceType: EnumServiceType): List<SummaryReportJobOrderItemDetails> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.getJobOrderItemDetailsServices(dateFilter.dateFrom, dateTo, serviceType)
    }

    suspend fun getJobOrderItemsDetailsExtras(dateFilter: DateFilter): List<SummaryReportJobOrderItemDetails> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.getJobOrderItemDetailsExtras(dateFilter.dateFrom, dateTo)
    }

    suspend fun getJobOrderItemsDetailsProducts(dateFilter: DateFilter): List<SummaryReportJobOrderItemDetails> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.getJobOrderItemDetailsProducts(dateFilter.dateFrom, dateTo)
    }

    suspend fun getJobOrderItemsDetailsDeliveries(dateFilter: DateFilter): List<SummaryReportJobOrderItemDetails> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.getJobOrderItemDetailsDeliveries(dateFilter.dateFrom, dateTo)
    }
}