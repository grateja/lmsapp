package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.customers.list.CustomerQueryResult
import com.vag.lmsapp.app.customers.list.advanced_filter.CustomersAdvancedFilter
import com.vag.lmsapp.room.dao.DaoCustomer
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.util.DateFilter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository
@Inject
constructor (
    private val daoCustomer: DaoCustomer,
) : BaseRepository<EntityCustomer>(daoCustomer) {
    override suspend fun get(id: UUID?) : EntityCustomer? {
        if(id == null) return null
        return daoCustomer.get(id)
    }

    override suspend fun save(entity: EntityCustomer): EntityCustomer? {
        entity.sync = false
        return super.save(entity)
    }

    fun getCustomerByPaymentIdAsLiveData(paymentId: UUID?) = daoCustomer.getCustomerByPaymentIdAsLiveData(paymentId)

//    suspend fun filter(keyword: String) : List<EntityCustomer> {
//        return daoCustomer.getAll(keyword)
//    }

    suspend fun getNextJONumber() : String {
        val currentCRN = daoCustomer.getLastCRN()?.toInt() ?: 0
        return (currentCRN + 1).toString().padStart(6, '0')
    }

    suspend fun checkName(name: String?) : Boolean {
        return daoCustomer.checkName(name)
    }

    suspend fun getCustomersMinimal(keyword: String?, page: Int, customerId: UUID?): List<CustomerMinimal> {
        val offset = (20 * page) - 20
        return daoCustomer.getCustomersMinimal(keyword, 20, offset, customerId)
    }

    suspend fun getListItems(keyword: String?, page: Int, advancedFilter: CustomersAdvancedFilter): CustomerQueryResult {
        val offset = (20 * page) - 20
        return daoCustomer.getListItem(keyword, offset, advancedFilter)
    }

    suspend fun getCustomerMinimalByCRN(crn: String?): EntityCustomer? {
        return daoCustomer.getCustomerMinimalByCRN(crn)
    }

    fun getDashboard(dateFilter: DateFilter) = daoCustomer.getDashboardCustomer(dateFilter.dateFrom, dateFilter.dateTo)

    fun getCustomerAsLiveData(customerId: UUID?) = daoCustomer.getCustomerAsLiveData(customerId)

    fun canCreateJobOrder(customerId: UUID, limit: Int) = daoCustomer.canCreateJobOrder(customerId, limit)
}