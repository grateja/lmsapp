package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class CustomerSyncService : SyncService("Sync", "Customer") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository
    @Inject lateinit var customerRepository: CustomerRepository

    companion object {
        fun start(context: Context, customerId: UUID?) {
            val intent = Intent(context, CustomerSyncService::class.java).apply {
                putExtra(ID, customerId.toString())
            }
            context.startForegroundService(intent)
        }
    }

    private fun startSync(id: UUID) {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val customer = customerRepository.get(id)

                if(token == null) {
                    safeStop()
                    return@runBlocking
                }

                if(shopId == null) {
                    safeStop()
                    return@runBlocking
                }

                if(customer == null) {
                    safeStop()
                    return@runBlocking
                }

                try {
                    networkRepository.sendCustomer(customer, shopId, token).let {
                        safeStop()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendUpdate("Failed to sync customer", e.message.toString())
                    safeStop()
                }
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(ID).toUUID()?.let {
            startSync(it)
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SVC_SYNC_NOTIFICATION_ID, getNotification("Sync started", "Updating customers data"))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}