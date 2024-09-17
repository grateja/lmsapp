package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.PAYMENT_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class JobOrderPaymentSyncService : SyncService("Sync", "Payment") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository
    @Inject lateinit var paymentRepository: PaymentRepository

    companion object {
        fun start(context: Context, paymentId: UUID?) {
            val intent = Intent(context, JobOrderPaymentSyncService::class.java).apply {
                this.action = action
                putExtra(PAYMENT_ID, paymentId.toString())
            }
            context.startForegroundService(intent)
        }
    }

    private fun syncPayment(paymentId: UUID) {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val payment = paymentRepository.getPaymentWithJobOrders(paymentId)

                if(token == null) {
                    println("No token")
                    safeStop()
                    return@runBlocking
                }

                if(shopId == null) {
                    println("Shop id cannot be null")
                    startForeground(1, getNotification("Shop's not setup yet", "Go to App settings and setup shop details"))
                    safeStop()
                    return@runBlocking
                }

                if(payment == null) {
                    println("Payment cannot be null")
                    safeStop()
                    return@runBlocking
                }

                val paymentRequestBody = PaymentRequestBody(
                    payment.payment,
                    payment.user,
                    payment.jobOrders.map { it.id }
                )

                try {
                    networkRepository.sendPayment(paymentRequestBody, shopId, token).let {
                        safeStop()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync job order payment", e.message.toString())
                    safeStop()
                }
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(PAYMENT_ID).toUUID()?.let {
            syncPayment(it)
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SVC_SYNC_NOTIFICATION_ID, getNotification("Sync started", "Updating job order payments."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}