package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class JobOrderSyncService: SyncService("Sync", "Job order") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository
    @Inject lateinit var jobOrderRepository: JobOrderRepository

    companion object {
        fun start(context: Context, jobOrderId: UUID) {
            val intent = Intent(context, JobOrderSyncService::class.java).apply {
                putExtra(JOB_ORDER_ID, jobOrderId.toString())
            }
            context.startForegroundService(intent)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(JOB_ORDER_ID).toUUID()?.let {
            sync(it)
        }
        return START_NOT_STICKY
    }

    private fun sync(jobOrderId: UUID) {
        Thread {
            runBlocking {
                try {
                    val shopId = shopRepository.get()?.id
                    val token = sanctumRepository.getSyncToken()
                    val jobOrder = jobOrderRepository.getJobOrderWithItems(jobOrderId)

                    if(token == null) {
                        println("No token")
                        safeStop()
                        return@runBlocking
                    }

                    if(shopId == null) {
                        safeStop()
                        return@runBlocking
                    }

                    if(jobOrder == null) {
                        println("Job order may be deleted")
                        safeStop()
                        return@runBlocking
                    }

                    networkRepository.sendJobOrder(jobOrder, shopId, token).let {result ->
                        safeStop()
                    }
                } catch (e: Exception) {
                    showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync job order", e.message.toString())
                    e.printStackTrace()
                    safeStop(60 * 5)
                }
            }
        }.start()
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SVC_SYNC_NOTIFICATION_ID, getNotification("Sync started", "Updating job orders data"))
    }
}