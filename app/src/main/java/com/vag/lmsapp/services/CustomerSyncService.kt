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
                this.action = action
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

                if(shopId == null) {
                    println("Shop id cannot be null")
                    return@runBlocking
                } else {
                    println("Shop id")
                    println(shopId)
                }

                if(customer == null) {
                    println("Payment cannot be null")
                    return@runBlocking
                } else {
                    println(customer)
                }

                if(token == null) {
                    println("No token")
                    return@runBlocking
                } else {
                    println("token")
                    println(token)
                }

                networkRepository.sendCustomer(customer, shopId, token).let {
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
        startForeground(1, getNotification("Sync", "Getting customer profile ready for sync."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}