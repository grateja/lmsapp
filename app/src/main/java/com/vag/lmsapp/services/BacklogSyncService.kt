package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class BacklogSyncService : SyncService(NAME, "Backlog") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BacklogSyncService::class.java)
            context.startForegroundService(intent)
        }

        const val NAME = "Backlog Sync service"

    }

    private suspend fun syncJobOrder(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.jobOrderCount()
            var current = 1
            while (true) {
                val jobOrder = networkRepository.getUnSyncJobOrder() ?: return true

                showNotification(UPT_SYNC_NOTIFICATION_ID,"Syncing job order ${current++}/$count", jobOrder.jobOrder.jobOrderNumber!!)

                val result = networkRepository.sendJobOrder(jobOrder, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }
        } catch (e: Exception) {
            showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync job Orders", e.message.toString())
            return false
        }
    }

    private suspend fun syncExpenses(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.expensesCount()
            var current = 1
            while (true) {
                val expense = networkRepository.getUnSyncExpense() ?: return true

                showNotification(UPT_SYNC_NOTIFICATION_ID, "Syncing expenses ${current++}/$count", expense.expense.remarks.toString())

                val result = networkRepository.sendExpense(expense, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }
        } catch (e: Exception) {
            showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync expenses", e.message.toString())
            return false
        }
    }

    private suspend fun syncPayment(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.jobPaymentCount()
            var current = 1
            while (true) {
                val payment = networkRepository.getUnSyncPayment() ?: return true

                showNotification(UPT_SYNC_NOTIFICATION_ID,"Syncing payment ${current++}/$count", "")

                val paymentRequest = PaymentRequestBody(
                    payment.payment,
                    payment.user,
                    payment.jobOrders.map {it.id}
                )
                val result = networkRepository.sendPayment(paymentRequest, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }
        } catch (e: Exception) {
            showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync job order payment", e.message.toString())
            return false
        }
    }

    private suspend fun syncMachineUsage(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.machineUsageCount()
            var current = 1
            while (true) {
                val machineUsage = networkRepository.getUnSyncMachineUsage() ?: return true

                showNotification(UPT_SYNC_NOTIFICATION_ID, "Syncing machine usage", "${current++}/$count")

                val result = networkRepository.sendMachineUsage(machineUsage, shopId, token)

                if(result.isFailure) {
                    return false
                }
            }
        } catch (e: Exception) {
            showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync machine usages", e.message.toString())
            return false
        }
    }

    private suspend fun syncInventoryLog(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.inventoryLogCount()
            var current = 1
            while (true) {
                val inventoryLog = networkRepository.getUnSyncInventoryLog() ?: return true

                showNotification(UPT_SYNC_NOTIFICATION_ID, "Syncing inventory log", "${current++}/$count")

                val result = networkRepository.sendInventoryLog(inventoryLog, shopId, token)

                if(result.isFailure) {
                    return false
                }
            }
        } catch (e: Exception) {
            showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync inventory log", e.message.toString())
            return false
        }
    }

    private fun startSync() {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()

                if(token == null) {
                    println("No token")
                    safeStop()
                    return@runBlocking
                }

                if(shopId == null) {
                    println("Shop id cannot be null")
                    showNotification(UPT_SYNC_NOTIFICATION_ID,"Shop's not setup yet", "Go to App settings and setup shop details")
                    safeStop()
                    return@runBlocking
                }

                if(syncJobOrder(shopId, token)
                    && syncPayment(shopId, token)
                    && syncMachineUsage(shopId, token)
                    && syncInventoryLog(shopId, token)
                    && syncExpenses(shopId, token)
                ) {
                    showNotification(UPT_SYNC_NOTIFICATION_ID,"Sync finished", "Data uploaded successful")
                    safeStop()
                } else {
                    safeStop()
                }
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSync()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SVC_SYNC_NOTIFICATION_ID, getNotification("Sync started", "Retrieving information..."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}