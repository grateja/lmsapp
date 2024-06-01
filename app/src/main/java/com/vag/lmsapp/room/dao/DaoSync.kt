package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import java.util.UUID

@Dao
interface DaoSync {
    @Query("UPDATE shops SET sync = 1")
    fun syncShop()

    @Query("UPDATE services SET sync = 1 WHERE id in (:ids)")
    fun syncServices(ids: List<UUID>?)

    @Query("UPDATE products SET sync = 1 WHERE id in (:ids)")
    fun syncProducts(ids: List<UUID>?)

    @Query("UPDATE extras SET sync = 1 WHERE id in (:ids)")
    fun syncExtras(ids: List<UUID>?)

    @Query("UPDATE delivery_profiles SET sync = 1 WHERE id in (:ids)")
    fun syncDeliveryProfiles(ids: List<UUID>?)

    @Query("UPDATE discounts SET sync = 1 WHERE id in (:ids)")
    fun syncDiscounts(ids: List<UUID>?)

    @Query("UPDATE job_orders SET sync = 1 WHERE id = :id")
    fun syncJobOrder(id: UUID?)

    @Query("UPDATE job_orders SET sync = 1 WHERE id in(:id)")
    fun syncJobOrder(id: List<UUID>?)

    @Query("UPDATE customers SET sync = 1 WHERE id = :id")
    fun syncCustomer(id: UUID?)

    @Query("UPDATE users SET sync = 1 WHERE id = :id")
    fun syncStaff(id: UUID?)

    @Query("UPDATE users SET sync = 1 WHERE id in(:ids)")
    fun syncStaff(ids: List<UUID>?)

    @Query("UPDATE job_order_payments SET sync = 1 WHERE id = :id")
    fun syncJobOrderPayment(id: UUID?)

    @Query("UPDATE job_order_services SET sync = 1 WHERE id in (:ids)")
    fun syncJobOrderServices(ids: List<UUID>?)

    @Query("UPDATE job_order_products SET sync = 1 WHERE id in (:ids)")
    fun syncJobOrderProducts(ids: List<UUID>?)

    @Query("UPDATE job_order_extras SET sync = 1 WHERE id in (:ids)")
    fun syncJobOrderExtras(ids: List<UUID>?)

    @Query("UPDATE job_order_discounts SET sync = 1 WHERE id = :id")
    fun syncJobOrderDiscount(id: UUID?)

    @Query("UPDATE job_order_delivery_charges SET sync = 1 WHERE id = :id")
    fun syncJobOrderDeliveryCharge(id: UUID?)

    @Transaction
    suspend fun syncJobOrder(jobOrderSyncIds: JobOrderSyncIds) {
        syncJobOrder(jobOrderSyncIds.jobOrderId)
        syncCustomer(jobOrderSyncIds.customerId)
        syncStaff(jobOrderSyncIds.createdBy)
        syncStaff(jobOrderSyncIds.paidBy)
        syncJobOrderDeliveryCharge(jobOrderSyncIds.deliveryChargeId)
        syncJobOrderDiscount(jobOrderSyncIds.discountId)
        syncJobOrderPayment(jobOrderSyncIds.paymentId)
        syncJobOrderServices(jobOrderSyncIds.servicesIds)
        syncJobOrderProducts(jobOrderSyncIds.productsIds)
        syncJobOrderExtras(jobOrderSyncIds.extrasIds)
    }

    @Transaction
    suspend fun syncPayment(paymentIds: PaymentSynIds) {
        syncJobOrder(paymentIds.jobOrderIds)
        syncStaff(paymentIds.paidBy)
        syncJobOrderPayment(paymentIds.paymentId)
    }

    @Transaction
    suspend fun syncSetup(setupSyncIds: SetupSyncIds) {
        syncShop()
        syncStaff(setupSyncIds.staffsIds)
        syncServices(setupSyncIds.servicesIds)
        syncProducts(setupSyncIds.productsIds)
        syncExtras(setupSyncIds.extrasIds)
        syncDeliveryProfiles(setupSyncIds.deliveryProfilesIds)
        syncDiscounts(setupSyncIds.discountsIds)
    }
}
