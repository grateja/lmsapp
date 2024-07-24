package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.payment_list.PaymentQueryResult
import com.vag.lmsapp.room.dao.DaoJobOrderPayment
import com.vag.lmsapp.room.entities.EntityJobOrderPayment
import com.vag.lmsapp.util.EnumSortDirection
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository
@Inject
constructor (
    private val daoPayment: DaoJobOrderPayment,
) {
    suspend fun get(id: UUID?): EntityJobOrderPayment? {
        return daoPayment.get(id)
    }

    fun getPaymentWithJobOrdersAsLiveData(id: UUID) = daoPayment.getPaymentWithJobOrdersAsLiveData(id)

    suspend fun getPaymentWithJobOrders(id: UUID) = daoPayment.getPaymentWithJobOrders(id)

    suspend fun save(payment: EntityJobOrderPayment, jobOrderIds: List<UUID>) : EntityJobOrderPayment {
        return daoPayment.save(payment, jobOrderIds)
    }

    suspend fun delete(paymentId: UUID, voidBy: UUID, remarks: String) {
        daoPayment.deletePayment(paymentId, voidBy, remarks)
    }

    suspend fun getByJobOrderId(jobOrderId: UUID?) = daoPayment.getByJobOrderId(jobOrderId)

    fun getCashlessProviders(): LiveData<List<String>> {
        return daoPayment.getCashlessProviders()
    }

    fun cashCollection(dateFilter: DateFilter) = daoPayment.getCashCollections(dateFilter.dateFrom, dateFilter.dateTo)

    fun cashlessPayments(dateFilter: DateFilter) = daoPayment.getCashlessPayments(dateFilter.dateFrom, dateFilter.dateTo)

    fun getJobOrdersByPaymentId(paymentId: UUID) = daoPayment.getJobOrdersByPaymentId(paymentId)

    suspend fun queryResult(keyword: String?, page: Int, dateFilter: DateFilter?, orderBy: String?, sortDirection: EnumSortDirection?) : PaymentQueryResult {
        val offset = (20 * page) - 20
        return daoPayment.queryResult(keyword, offset, dateFilter, orderBy, sortDirection)
    }
}