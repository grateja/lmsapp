package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.joborders.list.JobOrderQueryResult
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.model.JobOrderAdvancedFilter
import com.vag.lmsapp.room.dao.DaoJobOrder
import com.vag.lmsapp.room.entities.*
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderRepository
@Inject
constructor (
    private val daoJobOrder: DaoJobOrder,
) {
    suspend fun get(id: UUID?) : EntityJobOrder? {
        return daoJobOrder.get(id)
    }

    suspend fun getJobOrderWithItems(id: UUID?) : EntityJobOrderWithItems? {
        try {
            if(id == null) return null
            return daoJobOrder.getJobOrderWithItems(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getJobOrderWithItemsAsLiveData(id: UUID) = daoJobOrder.getJobOrderWithItemsAsLiveData(id)

//    suspend fun getAllWithTotalAmount(keyword: String, includeVoid :Boolean = false) : List<EntityJobOrderListItem> {
//        return listOf() //daoJobOrder.getAllWithTotalAmount(keyword, includeVoid)
//    }

    suspend fun getNextJONumber() : String {
        val currentJO = daoJobOrder.getLastJobOrderNumber()?.toInt() ?: 0
        return (currentJO + 1).toString().padStart(6, '0')
    }

    suspend fun save(jobOrderWithItem: EntityJobOrderWithItems) : EntityJobOrderWithItems? {
        try {
            daoJobOrder.save(jobOrderWithItem)
            return jobOrderWithItem
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

//    suspend fun saveAll(
//        joNumber: String,
//        customerId: UUID,
//        preparedBy: String,
//        services: List<MenuServiceItem>,
//        products: List<MenuProductItem>,
//        extras: List<MenuExtrasItem>,
//        delivery: DeliveryCharge,
//        discount: MenuDiscount) {
//
//    }

//    suspend fun void(jobOrder: EntityJobOrder) {
//        daoJobOrder.voidJobOrder(jobOrder)
//    }

    suspend fun getCurrentJobOrder(customerId: UUID?) : EntityJobOrderWithItems? {
        return daoJobOrder.getCurrentJobOrder(customerId)
    }

    fun getAllUnpaidByCustomerIdAsLiveData(customerId: UUID?) = daoJobOrder.getAllUnpaidByCustomerId(customerId)

    suspend fun getUnpaidByCustomerId(customerId: UUID): List<JobOrderPaymentMinimal> {
        return daoJobOrder.getUnpaidByCustomerId(customerId)
    }

    suspend fun load(
        keyword: String?,
        af: JobOrderAdvancedFilter,
//        orderBy: String?,
//        sortDirection: EnumSortDirection?,
        page: Int,
//        paymentStatus: EnumPaymentStatus?,
        customerId: UUID?,
        nonVoidOnly: Boolean,
//        filterBy: EnumJoFilterBy,
//        includeVoid: Boolean,
//        dateFilter: DateFilter?
    ): JobOrderQueryResult {
        val offset = (20 * page) - 20
        return daoJobOrder.queryResult(keyword, af, offset, customerId, nonVoidOnly)
    }
//    suspend fun load(
//        keyword: String?,
//        orderBy: String?,
//        sortDirection: EnumSortDirection?,
//        page: Int,
//        paymentStatus: EnumPaymentStatus?,
//        customerId: UUID?,
//        filterBy: EnumJoFilterBy,
//        includeVoid: Boolean,
//        dateFilter: DateFilter?
//    ): JobOrderQueryResult {
//        val offset = (20 * page) - 20
//        return daoJobOrder.queryResult(keyword, orderBy, sortDirection, offset, paymentStatus, customerId, filterBy, includeVoid, dateFilter?.dateFrom, dateFilter?.dateTo)
//    }

    suspend fun cancelJobOrder(jobOrderWithItem: EntityJobOrderWithItems, jobOrderVoid: EntityJobOrderVoid) {
        return daoJobOrder.cancelJobOrder(jobOrderWithItem, jobOrderVoid)
    }

    fun getPictures(jobOrderId: UUID?) = daoJobOrder.getPictures(jobOrderId)
    fun getPicturesAsLiveData(jobOrderId: UUID?) = daoJobOrder.getPicturesAsLiveData(jobOrderId)

    suspend fun attachPicture(jobOrderPictures: EntityJobOrderPictures) {
        return daoJobOrder.attachPicture(jobOrderPictures)
    }

    suspend fun removePicture(uriId: UUID) {
        daoJobOrder.removePicture(uriId)
    }
//
//    suspend fun removePicture(uriId: UUID) {
//        daoJobOrder.removePicture(uriId)
//    }

    suspend fun attachPictures(jobOrderPictures: List<EntityJobOrderPictures>) = daoJobOrder.attachPictures(jobOrderPictures)

    fun getDashboard(dateFilter: DateFilter) = daoJobOrder.getDashboardJobOrders(dateFilter.dateFrom, dateFilter.dateTo)

    fun getUnpaidJobOrdersAsLiveData(customerId: UUID) = daoJobOrder.getUnpaidJobOrdersAsLiveData(customerId)
}