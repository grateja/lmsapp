package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.daily_report.expenses.DailyReportExpenses
import com.vag.lmsapp.app.daily_report.extras.DailyReportExtras
import com.vag.lmsapp.app.daily_report.job_order.DailyReportJobOrder
import com.vag.lmsapp.app.daily_report.job_order_paid.DailyReportJobOrderPayment
import com.vag.lmsapp.app.daily_report.job_order_paid.DailyReportJobOrderPaymentSummary
import com.vag.lmsapp.app.daily_report.machine_usage.DailyReportMachineUsage
import com.vag.lmsapp.app.daily_report.pickup_delivery.DailyReportPickupDelivery
import com.vag.lmsapp.app.daily_report.products_chemicals.DailyReportProductsChemicals
import com.vag.lmsapp.app.daily_report.products_chemicals.DailyReportProductsChemicalsSummary
import com.vag.lmsapp.app.daily_report.wash_dry_services.DailyReportWashDryService
import com.vag.lmsapp.app.daily_report.wash_dry_services.DailyReportWashDrySummary
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.room.dao.DaoDailyReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyReportRepository

@Inject
constructor(
    private val dao: DaoDailyReport
) {
    fun jobOrder(dateFilter: DateFilter): LiveData<DailyReportJobOrder> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrder(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderPayment(dateFilter: DateFilter): LiveData<List<DailyReportJobOrderPayment>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderPayment(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderPaymentSummary(dateFilter: DateFilter): LiveData<DailyReportJobOrderPaymentSummary> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderPaymentSummary(dateFilter.dateFrom, dateTo)
    }

    fun washServices(dateFilter: DateFilter): LiveData<List<DailyReportWashDryService>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.washServices(dateFilter.dateFrom, dateTo)
    }

    fun dryServices(dateFilter: DateFilter): LiveData<List<DailyReportWashDryService>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.dryServices(dateFilter.dateFrom, dateTo)
    }

    fun extras(dateFilter: DateFilter): LiveData<List<DailyReportExtras>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.extras(dateFilter.dateFrom, dateTo)
    }

    fun productsChemicals(dateFilter: DateFilter, productType: EnumProductType): LiveData<List<DailyReportProductsChemicals>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.productsChemicals(dateFilter.dateFrom, dateTo, productType)
    }

    fun productsChemicalsSummary(dateFilter: DateFilter): LiveData<DailyReportProductsChemicalsSummary> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.productsChemicalsSummary(dateFilter.dateFrom, dateTo)
    }

    fun delivery(dateFilter: DateFilter): LiveData<List<DailyReportPickupDelivery>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.delivery(dateFilter.dateFrom, dateTo)
    }

    fun machineUsages(dateFilter: DateFilter): LiveData<List<DailyReportMachineUsage>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.machineUsages(dateFilter.dateFrom, dateTo)
    }

    fun expenses(dateFilter: DateFilter): LiveData<List<DailyReportExpenses>> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.expenses(dateFilter.dateFrom, dateTo)
    }
}