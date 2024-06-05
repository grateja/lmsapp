package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.network.responses.InventoryLogSyncIds
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.responses.MachineUsageSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
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

    @Query("UPDATE job_orders SET sync = 1 WHERE id = :id")
    suspend fun syncJobOrder(id: UUID?)

    @Query("UPDATE job_orders SET sync = 1 WHERE id in(:id)")
    suspend fun syncJobOrder(id: List<UUID>?)

    @Query("UPDATE customers SET sync = 1 WHERE id = :id")
    suspend fun syncCustomer(id: UUID?)

    @Query("UPDATE users SET sync = 1 WHERE id = :id")
    suspend fun syncStaff(id: UUID?)

    @Query("UPDATE users SET sync = 1 WHERE id in(:ids)")
    suspend fun syncStaff(ids: List<UUID>?)

    @Query("UPDATE job_order_payments SET sync = 1 WHERE id = :id")
    suspend fun syncJobOrderPayment(id: UUID?)

    @Query("UPDATE machine_usages SET sync = 1 WHERE id = :id")
    suspend fun syncMachineUsage(id: UUID?)

    @Query("UPDATE expenses SET sync = 1 WHERE id = :id")
    suspend fun syncExpense(id: UUID)

    @Query("UPDATE inventory_log SET sync = 1 WHERE id = :id")
    suspend fun syncInventoryLog(id: UUID)

    @Transaction
    suspend fun syncJobOrder(jobOrderSyncIds: JobOrderSyncIds) {
        syncJobOrder(jobOrderSyncIds.jobOrderId)
        syncCustomer(jobOrderSyncIds.customerId)
        syncStaff(jobOrderSyncIds.createdBy)
        syncStaff(jobOrderSyncIds.paidBy)
        syncJobOrderPayment(jobOrderSyncIds.paymentId)
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

    @Transaction
    suspend fun syncMachineUsage(machineUsageIds: MachineUsageSyncIds) {
        syncCustomer(machineUsageIds.customerId)
        syncMachineUsage(machineUsageIds.machineUsageId)
    }

    @Transaction
    suspend fun syncInventoryLog(response: InventoryLogSyncIds) {
        syncProducts(listOf(response.productId))
        response.expenseId?.let {
            syncExpense(it)
        }
        syncInventoryLog(response.inventoryLogId)
        syncStaff(response.userId)
    }

    @Query("SELECT * from job_orders WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun getUnSyncJobOrder(): EntityJobOrderWithItems?

    @Query("SELECT * from machine_usages WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun getUnSyncMachineUsage(): EntityMachineUsageFull?

    @Query("SELECT * from inventory_log WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun getUnSyncInventoryLog(): EntityInventoryLogFull?

    @Query("SELECT * from job_order_payments WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun getUnSyncPayment(): EntityJobOrderPaymentFull?

    @Query("SELECT * from expenses WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun getUnSyncExpense(): EntityExpenseFull?

    @Query("SELECT COUNT(*) from job_orders WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun jobOrderCount(): Int

    @Query("SELECT COUNT(*) from machine_usages WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun machineUsageCount(): Int

    @Query("SELECT COUNT(*) from inventory_log WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun inventoryLogCount(): Int

    @Query("SELECT COUNT(*) from job_order_payments WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun paymentCount(): Int

    @Query("SELECT COUNT(*) from expenses WHERE sync = 0 ORDER BY created_at DESC LIMIT 1")
    suspend fun expensesCount(): Int
}
