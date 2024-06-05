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
//    @Inject lateinit var serviceRepository: WashServiceRepository
//    @Inject lateinit var productRepository: ProductRepository
//    @Inject lateinit var extraRepository: ExtrasRepository
//    @Inject lateinit var deliveryRepository: DeliveryProfilesRepository
//    @Inject lateinit var discountRepository: DiscountsRepository

    companion object {
//        const val CHANNEL_ID = "live_sync_channel_id"
//        const val NOTIFICATION_ID = 104
//
//        const val ACTION_SYNC_PAYMENT = "action_sync_payment"
//        const val ACTION_SYNC_SETUP = "action_sync_bulk_payload"
//
        fun start(context: Context, paymentId: UUID?) {
            val intent = Intent(context, JobOrderPaymentSyncService::class.java).apply {
                this.action = action
                putExtra(PAYMENT_ID, paymentId.toString())
            }
            context.startForegroundService(intent)
        }
    }

//    private val notificationManager by lazy {
//        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//    private fun createChannel() =
//        NotificationChannel(
//            CHANNEL_ID,
//            "Live Sync Channel",
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Syncing data base"
//            setSound(null, null)
//        }

//    private fun getNotification(message: String?): Notification {
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Sync started")
//            .setContentText(message)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true)
//            .setAutoCancel(true)
//
//        notificationManager.createNotificationChannel(createChannel())
//        return builder.build()
//    }

    private fun syncPayment(paymentId: UUID) {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val payment = paymentRepository.getPaymentWithJobOrders(paymentId)

                if(shopId == null) {
                    println("Shop id cannot be null")
                    return@runBlocking
                } else {
                    println("Shop id")
                    println(shopId)
                }

                if(payment == null) {
                    println("Payment cannot be null")
                    return@runBlocking
                } else {
                    println(payment)
                }

                if(token == null) {
                    println("No token")
                    return@runBlocking
                } else {
                    println("token")
                    println(token)
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
                    startForeground(1, getNotification("Sync", e.message.toString()))
                    safeStop(10)
                }
            }
        }.start()
    }

//    private fun syncBulkPayload() {
//        Thread {
//            runBlocking {
//                val shopId = shopRepository.get()?.id
//                val token = sanctumRepository.getSyncToken()
//                val services = serviceRepository.unSynced()
//                val products = productRepository.unSynced()
//                val extras = extraRepository.unSynced()
//                val deliveryProfiles = deliveryRepository.unSynced()
//                val discounts = discountRepository.unSynced()
//
//                if(shopId == null) {
//                    println("Shop id cannot be null")
//                    return@runBlocking
//                } else {
//                    println("Shop id")
//                    println(shopId)
//                }
//
//                if(token == null) {
//                    println("No token")
//                    return@runBlocking
//                } else {
//                    println("token")
//                    println(token)
//                }
//
//                val payload = BulkPayload(
//                    services,
//                    products,
//                    extras,
//                    deliveryProfiles,
//                    discounts
//                )
//
//                networkRepository.sendBulkPayload(payload, shopId, token).let {
//                    safeStop()
//                }
//            }
//        }.start()
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if(intent?.action == ACTION_SYNC_JOB_ORDER) {
//            intent.getStringExtra(ID).toUUID()?.let {
//                startForeground(NOTIFICATION_ID, getNotification("Syncing Job Order..."))
//                syncJobOrder(it)
//            }
//        } else
//        if(intent?.action == ACTION_SYNC_PAYMENT) {
//            intent.getStringExtra(ID).toUUID()?.let {
//                startForeground(NOTIFICATION_ID, getNotification("Syncing Payment..."))
//                syncPayment(it)
//            }
//        } else if(intent?.action == ACTION_SYNC_SETUP) {
//            startForeground(NOTIFICATION_ID, getNotification("Syncing Setup..."))
//            TODO("there's no such function")
////            syncBulkPayload()
//        }
        intent?.getStringExtra(PAYMENT_ID).toUUID()?.let {
            syncPayment(it)
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, getNotification("Sync", "Getting job order payment ready for sync."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}