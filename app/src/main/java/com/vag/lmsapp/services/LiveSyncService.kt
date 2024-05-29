package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vag.lmsapp.R
import com.vag.lmsapp.network.BulkPayload
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.PaymentRequestBody
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.room.repository.WashServiceRepository
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class LiveSyncService : Service() {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository
    @Inject lateinit var jobOrderRepository: JobOrderRepository
    @Inject lateinit var paymentRepository: PaymentRepository
    @Inject lateinit var serviceRepository: WashServiceRepository
    @Inject lateinit var productRepository: ProductRepository
    @Inject lateinit var extraRepository: ExtrasRepository
    @Inject lateinit var deliveryRepository: DeliveryProfilesRepository
    @Inject lateinit var discountRepository: DiscountsRepository

    companion object {
        const val CHANNEL_ID = "live_sync_channel_id"
        const val NOTIFICATION_ID = 104

        const val ACTION_SYNC_JOB_ORDER = "action_sync_job_order"
        const val ACTION_SYNC_PAYMENT = "action_sync_payment"
        const val ACTION_SYNC_SETUP = "action_sync_bulk_payload"

        fun getIntent(context: Context, action: String, id: UUID?) : Intent {
            return Intent(context, LiveSyncService::class.java).apply {
                this.action = action
                putExtra(ID, id.toString())
            }
        }
    }

    private val notificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private fun createChannel() =
        NotificationChannel(
            CHANNEL_ID,
            "Live Sync Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Syncing data base"
            setSound(null, null)
        }

    private fun getNotification(message: String?): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sync started")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(true)

        notificationManager.createNotificationChannel(createChannel())
        return builder.build()
    }

    private fun syncJobOrder(jobOrderId: UUID) {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val jobOrder = jobOrderRepository.getJobOrderWithItems(jobOrderId)

                if(shopId == null) {
                    println("Shop id cannot be null")
                    return@runBlocking
                } else {
                    println("Shop id")
                    println(shopId)
                }

                if(jobOrder == null) {
                    println("Job order cannot be null")
                    return@runBlocking
                } else {
                    println(jobOrder)
                }

                if(token == null) {
                    println("No token")
                    return@runBlocking
                } else {
                    println("token")
                    println(token)
                }

                networkRepository.sendJobOrder(jobOrder, shopId, token).let {
                    safeStop()
                }
            }
        }.start()
    }

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

                networkRepository.sendPayment(paymentRequestBody, shopId, token).let {
                    safeStop()
                }
            }
        }.start()
    }

    private fun syncBulkPayload() {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val services = serviceRepository.unSynced()
                val products = productRepository.unSynced()
                val extras = extraRepository.unSynced()
                val deliveryProfiles = deliveryRepository.unSynced()
                val discounts = discountRepository.unSynced()

                if(shopId == null) {
                    println("Shop id cannot be null")
                    return@runBlocking
                } else {
                    println("Shop id")
                    println(shopId)
                }

                if(token == null) {
                    println("No token")
                    return@runBlocking
                } else {
                    println("token")
                    println(token)
                }

                val payload = BulkPayload(
                    services,
                    products,
                    extras,
                    deliveryProfiles,
                    discounts
                )

                networkRepository.sendBulkPayload(payload, shopId, token).let {
                    safeStop()
                }
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == ACTION_SYNC_JOB_ORDER) {
            intent.getStringExtra(ID).toUUID()?.let {
                startForeground(NOTIFICATION_ID, getNotification("Syncing Job Order..."))
                syncJobOrder(it)
            }
        } else if(intent?.action == ACTION_SYNC_PAYMENT) {
            intent.getStringExtra(ID).toUUID()?.let {
                startForeground(NOTIFICATION_ID, getNotification("Syncing Payment..."))
                syncPayment(it)
            }
        } else if(intent?.action == ACTION_SYNC_SETUP) {
            startForeground(NOTIFICATION_ID, getNotification("Syncing Setup..."))
            syncBulkPayload()
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification("Sync ready!"))
    }

    private fun safeStop() {
        Thread {
            runBlocking {
                delay(1000L)
                stopSelf()
            }
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}