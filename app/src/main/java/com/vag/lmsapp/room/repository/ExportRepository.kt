package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.export_options.data.ExportDataDeliveryCharge
import com.vag.lmsapp.app.export_options.data.ExportDataExpenses
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrder
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderExtras
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderProduct
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderService
import com.vag.lmsapp.app.export_options.data.ExportDataMachineUsage
import com.vag.lmsapp.app.export_options.data.ExportDataNewCustomers
import com.vag.lmsapp.room.dao.DaoExport
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepository

@Inject
constructor(private val dao: DaoExport)
{
    suspend fun jobOrders(dateFilter: DateFilter): List<ExportDataJobOrder> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrders(dateFilter.dateFrom, dateTo)
    }

    suspend fun jobOrderServices(dateFilter: DateFilter): List<ExportDataJobOrderService> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderServices(dateFilter.dateFrom, dateTo)
    }

    suspend fun jobOrderProducts(dateFilter: DateFilter): List<ExportDataJobOrderProduct> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderProducts(dateFilter.dateFrom, dateTo)
    }

    suspend fun jobOrderExtras(dateFilter: DateFilter): List<ExportDataJobOrderExtras> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderExtras(dateFilter.dateFrom, dateTo)
    }

    suspend fun machineUsages(dateFilter: DateFilter): List<ExportDataMachineUsage> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.machineUsages(dateFilter.dateFrom, dateTo)
    }

    suspend fun deliveryCharges(dateFilter: DateFilter): List<ExportDataDeliveryCharge> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.deliveryCharges(dateFilter.dateFrom, dateTo)
    }

    suspend fun expenses(dateFilter: DateFilter): List<ExportDataExpenses> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.expenses(dateFilter.dateFrom, dateTo)
    }

    suspend fun customers(dateFilter: DateFilter): List<ExportDataNewCustomers> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.customers(dateFilter.dateFrom, dateTo)
    }

    fun jobOrdersCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrdersCount(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderServicesCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderServicesCount(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderProductsCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderProductsCount(dateFilter.dateFrom, dateTo)
    }

    fun jobOrderExtrasCount(dateFilter: DateFilter): LiveData<Int>  {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.jobOrderExtrasCount(dateFilter.dateFrom, dateTo)
    }

    fun machineUsagesCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.machineUsagesCount(dateFilter.dateFrom, dateTo)
    }

    fun deliveryChargesCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.deliveryChargesCount(dateFilter.dateFrom, dateTo)
    }

    fun expensesCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.expensesCount(dateFilter.dateFrom, dateTo)
    }

    fun customersCount(dateFilter: DateFilter): LiveData<Int> {
        val dateTo = dateFilter.dateTo ?: dateFilter.dateFrom
        return dao.customersCount(dateFilter.dateFrom, dateTo)
    }
}