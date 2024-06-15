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

    fun washServices(date: LocalDate) = dao.washServices(date)

    fun dryServices(date: LocalDate) = dao.dryServices(date)

    fun extras(date: LocalDate) = dao.extras(date)

    fun productsChemicals(date: LocalDate, productType: EnumProductType) = dao.productsChemicals(date, productType)

    fun productsChemicalsSummary(date: LocalDate) = dao.productsChemicalsSummary(date)

    fun delivery(date: LocalDate) = dao.delivery(date)

    fun machineUsages(date: LocalDate) = dao.machineUsages(date)

    fun expenses(date: LocalDate) = dao.expenses(date)
}