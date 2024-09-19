package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.app.lms_live.sync.SyncCounts
import com.vag.lmsapp.network.responses.InventoryLogSyncIds
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.responses.MachineUsageSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.entities.EntityExpenseFull
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.room.entities.EntityJobOrderPaymentFull
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.entities.EntityMachineUsageFull
import java.util.UUID

@Dao
interface DaoSync {
    @Query("UPDATE shops SET sync = 1")
    suspend fun syncShop()

    @Query("UPDATE services SET sync = 1 WHERE id in (:ids)")
    suspend fun syncServices(ids: List<UUID>?)

    @Query("UPDATE products SET sync = 1 WHERE id in (:ids)")
    suspend fun syncProducts(ids: List<UUID>?)

    @Query("UPDATE extras SET sync = 1 WHERE id in (:ids)")
    suspend fun syncExtras(ids: List<UUID>?)

    @Query("UPDATE delivery_profiles SET sync = 1 WHERE id in (:ids)")
    suspend fun syncDeliveryProfiles(ids: List<UUID>?)

    @Query("UPDATE discounts SET sync = 1 WHERE id in (:ids)")
    suspend fun syncDiscounts(ids: List<UUID>?)

    @Query("UPDATE job_orders SET sync = 1 WHERE id in(:ids)")
    suspend fun syncJobOrderIds(ids: List<UUID>)

    @Query("UPDATE customers SET sync = 1 WHERE id IN (:ids)")
    suspend fun syncCustomers(ids: List<UUID>)

    @Query("UPDATE users SET sync = 1 WHERE id in(:ids)")
    suspend fun syncStaff(ids: List<UUID>?)

    @Query("UPDATE job_order_payments SET sync = 1 WHERE id in(:ids)")
    suspend fun syncJobOrderPayment(ids: List<UUID>?)

    @Query("UPDATE machine_usages SET sync = 1 WHERE id in(:ids)")
    suspend fun syncMachineUsage(ids: List<UUID>)

    @Query("UPDATE expenses SET sync = 1 WHERE id in(:ids)")
    suspend fun syncExpenses(ids: List<UUID>)

    @Query("UPDATE inventory_log SET sync = 1 WHERE id in(:ids)")
    suspend fun syncInventoryLog(ids: List<UUID>)

    @Query("UPDATE machines SET sync = 1 WHERE id IN(:ids)")
    suspend fun syncMachines(ids: List<UUID>?)

    @Transaction
    suspend fun syncJobOrders(jobOrderSyncIds: JobOrderSyncIds) {
        syncJobOrderIds(jobOrderSyncIds.jobOrderIds)
        syncCustomers(jobOrderSyncIds.customerIds)
        syncStaff(jobOrderSyncIds.staffIds)
//        syncStaff(jobOrderSyncIds.paidBys)
        syncJobOrderPayment(jobOrderSyncIds.paymentIds)
    }

    @Transaction
    suspend fun syncPayment(paymentIds: PaymentSynIds) {
        syncJobOrderIds(paymentIds.jobOrderIds)
        syncStaff(paymentIds.staffIds)
        syncJobOrderPayment(paymentIds.paymentIds)
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
        syncMachines(setupSyncIds.machineIds)
    }

    @Transaction
    suspend fun syncMachineUsage(machineUsageIds: MachineUsageSyncIds) {
        syncCustomers(machineUsageIds.customerIds)
        syncMachineUsage(machineUsageIds.machineUsageIds)
    }

    @Transaction
    suspend fun syncInventoryLog(response: InventoryLogSyncIds) {
        syncProducts(response.productIds)
        syncExpenses(response.expenseIds)
        syncInventoryLog(response.inventoryLogIds)
        syncStaff(response.staffIds)
    }

    @Query("SELECT * from customers WHERE sync = 0 ORDER BY created_at DESC LIMIT 100")
    suspend fun getUnSyncCustomers(): List<EntityCustomer>

    @Query("SELECT * from job_orders WHERE sync = 0 ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUnSyncJobOrders(limit: Int): List<EntityJobOrderWithItems>

    @Query("SELECT * from machine_usages WHERE sync = 0 ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUnSyncMachineUsage(limit: Int): List<EntityMachineUsageFull>

    @Query("SELECT * from inventory_log WHERE sync = 0 ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUnSyncInventoryLog(limit: Int): List<EntityInventoryLogFull>

    @Query("SELECT * from job_order_payments WHERE sync = 0 ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUnSyncPayment(limit: Int): List<EntityJobOrderPaymentFull>

    @Query("SELECT * from expenses WHERE sync = 0 ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUnSyncExpenses(limit: Int): List<EntityExpenseFull>

    @Query("SELECT COUNT(*) from job_orders WHERE sync = 0")
    suspend fun jobOrderCount(): Int

    @Query("SELECT COUNT(*) from machine_usages WHERE sync = 0")
    suspend fun machineUsageCount(): Int

    @Query("SELECT COUNT(*) from inventory_log WHERE sync = 0")
    suspend fun inventoryLogCount(): Int

    @Query("SELECT COUNT(*) from job_order_payments WHERE sync = 0")
    suspend fun paymentCount(): Int

    @Query("SELECT COUNT(*) from expenses WHERE sync = 0")
    suspend fun expensesCount(): Int

    @Query("SELECT COUNT(*) from customers WHERE sync = 0")
    suspend fun customerCount(): Int

    @Query("SELECT COUNT(*) from job_orders WHERE sync = 0")
    fun jobOrderCountAsLiveData(): LiveData<Int>

    @Query("SELECT COUNT(*) from machine_usages WHERE sync = 0")
    fun machineUsageCountAsLiveData(): LiveData<Int>

    @Query("SELECT COUNT(*) from inventory_log WHERE sync = 0")
    fun inventoryLogCountAsLiveData(): LiveData<Int>

    @Query("SELECT COUNT(*) from job_order_payments WHERE sync = 0")
    fun paymentCountAsLiveData(): LiveData<Int>

    @Query("SELECT COUNT(*) from expenses WHERE sync = 0")
    fun expensesCountAsLiveData(): LiveData<Int>

    @Query("SELECT COUNT(*) from customers WHERE sync = 0")
    fun customerCountAsLiveData(): LiveData<Int>



    @Query("""
    SELECT (SELECT COUNT(*) FROM job_orders WHERE sync = 0) AS jobOrderCount,
           (SELECT COUNT(*) FROM machine_usages WHERE sync = 0) AS machineUsageCount,
           (SELECT COUNT(*) FROM inventory_log WHERE sync = 0) AS inventoryLogCount,
           (SELECT COUNT(*) FROM job_order_payments WHERE sync = 0) AS paymentCount,
           (SELECT COUNT(*) FROM expenses WHERE sync = 0) AS expensesCount,
           (SELECT COUNT(*) FROM customers WHERE sync = 0) AS customerCount
    """)
    fun getAllCounts(): LiveData<SyncCounts>
}
