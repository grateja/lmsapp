package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.periodicAsyncTask
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

    private suspend fun syncCustomer(shopId: UUID, token: String): Boolean {
        try {
            val count = (networkRepository.customerCount() / 100) + 1

            count.periodicAsyncTask(20) { step, iteration ->
                val body = networkRepository.getUnSyncCustomers()

                if(body.isEmpty()) return@periodicAsyncTask

                if(step) {
                    notifyProgress("Sync started", "Updating customer data", count, iteration)
                }

                val result = networkRepository.sendCustomer(body, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true

//            while (true) {
//                val body = networkRepository.getUnSyncCustomers()
//
//                if(body.isEmpty()) return true
//
//                sendUpdate("Sync started", "Updating customer data")
//
//                val result = networkRepository.sendCustomer(body, shopId, token)
//
//                if(result.isFailure) {
//                    throw Exception(result.exceptionOrNull())
//                }
//            }
        } catch (e: Exception) {
            sendUpdate("Failed to sync customer", e.message.toString())
            e.printStackTrace()
            return false
        }
    }

    private suspend fun syncJobOrder(shopId: UUID, token: String): Boolean {
        try {
            val count = (networkRepository.jobOrderCount() / 30) + 1

            count.periodicAsyncTask(20) {stepped, iteration ->
                val jobOrder = networkRepository.getUnSyncJobOrders(30)

                if(jobOrder.isEmpty()) return@periodicAsyncTask

                if(stepped) {
                    notifyProgress("Sync started", "Updating job orders data", count, iteration)
                }

//                sendUpdate("Syncing job order ${iteration}/$count", jobOrder.jobOrder.jobOrderNumber!!)

                val result = networkRepository.sendJobOrder(jobOrder, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true
//            var current = 1
//            while (true) {
//                val jobOrder = networkRepository.getUnSyncJobOrder() ?: return true
//
//                sendUpdate("Syncing job order ${current++}/$count", jobOrder.jobOrder.jobOrderNumber!!)
//
//                val result = networkRepository.sendJobOrder(jobOrder, shopId, token)
//
//                if(result.isFailure) {
//                    throw Exception(result.exceptionOrNull())
//                }
//            }
        } catch (e: Exception) {
            sendUpdate("Failed to sync job orders", e.message.toString())
            return false
        }
    }

    private suspend fun syncExpenses(shopId: UUID, token: String): Boolean {
        try {
            val count = (networkRepository.expensesCount() / 30) + 1

            count.periodicAsyncTask(20) { stepped, iteration ->
                val expense = networkRepository.getUnSyncExpenses(30)

                if(expense.isEmpty()) return@periodicAsyncTask
                if(stepped) {
                    notifyProgress( "Syncing started", "Updating expenses", count, iteration)
                }

                val result = networkRepository.sendExpense(expense, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true

//            while (true) {
//                val expense = networkRepository.getUnSyncExpenses(30) ?: return true
//
//                sendUpdate( "Syncing expenses ${current++}/$count", expense.expense.remarks.toString())
//
//                val result = networkRepository.sendExpense(expense, shopId, token)
//
//                if(result.isFailure) {
//                    throw Exception(result.exceptionOrNull())
//                }
//            }
        } catch (e: Exception) {
            sendUpdate("Failed to sync expenses", e.message.toString())
            return false
        }
    }

    private suspend fun syncPayment(shopId: UUID, token: String): Boolean {
        try {
            val limit = 20
            val count = (networkRepository.jobPaymentCount() / limit) + 1

            count.periodicAsyncTask(20) { stepped, iteration ->
                val payments = networkRepository.getUnSyncPayment(limit)
                if(payments.isEmpty()) return@periodicAsyncTask

                if(stepped) {
                    notifyProgress( "Syncing started", "Updating payments", count, iteration)
                }

                val paymentRequest = payments.map {payment ->
                    PaymentRequestBody(
                        payment.payment,
                        payment.user,
                        payment.jobOrders.map {it.id}
                    )
                }
                val result = networkRepository.sendPayment(paymentRequest, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true
//            while (true) {
//                val payments = networkRepository.getUnSyncPayment(limit)
//
//                sendUpdate("Syncing payment ${current++}/$count", "")
//
//                val paymentRequest = payments.map {payment ->
//                    PaymentRequestBody(
//                        payment.payment,
//                        payment.user,
//                        payment.jobOrders.map {it.id}
//                    )
//                }
//                val result = networkRepository.sendPayment(paymentRequest, shopId, token)
//
//                if(result.isFailure) {
//                    throw Exception(result.exceptionOrNull())
//                }
//            }
        } catch (e: Exception) {
            sendUpdate("Failed to sync job order payment", e.message.toString())
            return false
        }
    }

    private suspend fun syncMachineUsage(shopId: UUID, token: String): Boolean {
        try {
            val limit = 50
            val count = (networkRepository.machineUsageCount() / limit) + 1

            count.periodicAsyncTask(20) {stepped, iteration ->
                val machineUsage = networkRepository.getUnSyncMachineUsage(limit)

                if(machineUsage.isEmpty()) return@periodicAsyncTask

                if(stepped) {
                    notifyProgress("Sync started", "Updating machine usage", count, iteration)
                }

                val result = networkRepository.sendMachineUsage(machineUsage, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true
        } catch (e: Exception) {
            sendUpdate("Failed to sync machine usages", e.message.toString())
            return false
        }
    }

    private suspend fun syncInventoryLog(shopId: UUID, token: String): Boolean {
        try {
            val limit = 100
            val count = (networkRepository.inventoryLogCount() / limit) + 1

            count.periodicAsyncTask(20) {stepped, iteration ->
                val inventoryLog = networkRepository.getUnSyncInventoryLog(limit)

                if(inventoryLog.isEmpty()) return@periodicAsyncTask

                if(stepped) {
                    notifyProgress("Sync started", "Updating inventory", count, iteration)
                }

                val result = networkRepository.sendInventoryLog(inventoryLog, shopId, token)

                if(result.isFailure) {
                    throw Exception(result.exceptionOrNull())
                }
            }

            return true

//            while (true) {
//
//                sendUpdate( "Syncing inventory log", "${current++}/$count")
//
//                val result = networkRepository.sendInventoryLog(inventoryLog, shopId, token)
//
//                if(result.isFailure) {
//                    return false
//                }
//            }
        } catch (e: Exception) {
            sendUpdate("Failed to sync inventory log", e.message.toString())
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
                    sendUpdate("Shop's not setup yet", "Go to App settings and setup shop details")
                    safeStop()
                    return@runBlocking
                }

                if(syncCustomer(shopId, token)
                    && syncJobOrder(shopId, token)
                    && syncPayment(shopId, token)
                    && syncMachineUsage(shopId, token)
                    && syncInventoryLog(shopId, token)
                    && syncExpenses(shopId, token)
                ) {
                    sendUpdate("Sync finished", "Data uploaded successful")
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