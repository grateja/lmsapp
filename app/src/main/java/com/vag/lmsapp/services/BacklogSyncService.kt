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
class BacklogSyncService : SyncService("Sync", "Backlog") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BacklogSyncService::class.java)
            context.startForegroundService(intent)
        }
    }

    private suspend fun syncJobOrder(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.jobOrderCount()
            var current = 1
            while (true) {
                val jobOrder = networkRepository.getUnSyncJobOrder()
                if (jobOrder == null) {
                    // all done
                    return true
                } else {
                    startForeground(1, getNotification("Syncing job order ${current++}/$count", jobOrder.jobOrder.jobOrderNumber!!))
                    val result = networkRepository.sendJobOrder(jobOrder, shopId, token)

                    if(result.isFailure) {
                        throw Exception(result.exceptionOrNull())
                    }
                }
            }
        } catch (e: Exception) {
            startForeground(1, getNotification("Something went wrong during sync", ""))
            // Handle exceptions such as network errors or unexpected issues
            println("An error occurred during job order synchronization: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    private suspend fun syncExpenses(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.expensesCount()
            var current = 1
            while (true) {
                val expense = networkRepository.getUnSyncExpense()
                if (expense == null) {
                    // all done
                    return true
                } else {
                    startForeground(1, getNotification("Syncing expenses ${current++}/$count", expense.expense.remarks.toString()))
                    val result = networkRepository.sendExpense(expense, shopId, token)

                    if(result.isFailure) {
                        throw Exception(result.exceptionOrNull())
                    }
                }
            }
        } catch (e: Exception) {
            startForeground(1, getNotification("Something went wrong during sync", ""))
            // Handle exceptions such as network errors or unexpected issues
            println("An error occurred during job order synchronization: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    private suspend fun syncPayment(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.jobPaymentCount()
            var current = 1
            while (true) {
                val payment = networkRepository.getUnSyncPayment()
                if (payment == null) {
                    // all done
                    return true
                } else {
                    startForeground(1, getNotification("Syncing payment ${current++}/$count", ""))
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
            }
        } catch (e: Exception) {
            startForeground(1, getNotification("Something went wrong during sync", ""))
            // Handle exceptions such as network errors or unexpected issues
            println("An error occurred during job order payment synchronization: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    private suspend fun syncMachineUsage(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.machineUsageCount()
            var current = 1
            while (true) {
                val machineUsage = networkRepository.getUnSyncMachineUsage()
                if (machineUsage == null) {
                    // all done
                    return true
                } else {
                    startForeground(1, getNotification("Syncing machine usage", "${current++}/$count"))
                    val result = networkRepository.sendMachineUsage(machineUsage, shopId, token)
                    if(result.isFailure) {
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private suspend fun syncInventoryLog(shopId: UUID, token: String): Boolean {
        try {
            val count = networkRepository.inventoryLogCount()
            var current = 1
            while (true) {
                val inventoryLog = networkRepository.getUnSyncInventoryLog()
                if (inventoryLog == null) {
                    // all done
                    return true
                } else {
                    startForeground(1, getNotification("Syncing inventory log", "${current++}/$count"))
                    val result = networkRepository.sendInventoryLog(inventoryLog, shopId, token)
                    if(result.isFailure) {
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                    safeStop(1)
                    return@runBlocking
                } else {
                    println("token")
                    println(token)
                }

                if(shopId == null) {
                    println("Shop id cannot be null")
                    startForeground(1, getNotification("Shop's not setup yet", "Go to App settings and setup shop details"))
                    return@runBlocking
                } else {
                    println("Shop id")
                    println(shopId)
                }

                if(syncJobOrder(shopId, token) && syncPayment(shopId, token) && syncMachineUsage(shopId, token) && syncInventoryLog(shopId, token) && syncExpenses(shopId, token)) {
                    startForeground(1, getNotification("Sync", "Sync successful"))
                    safeStop(30)
                } else {
                    safeStop(60 * 5)
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
        startForeground(1, getNotification("Sync", "Getting customer profile ready for sync."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}