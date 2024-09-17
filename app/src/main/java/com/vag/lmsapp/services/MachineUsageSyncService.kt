package com.vag.lmsapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MachineUsageSyncService : SyncService("Sync", "Machine usage") {
    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var sanctumRepository: SanctumRepository
    @Inject lateinit var shopRepository: ShopRepository
    @Inject lateinit var machineRepository: MachineRepository

    companion object {
        fun start(context: Context, id: UUID?) {
            val intent = Intent(context, MachineUsageSyncService::class.java).apply {
                this.action = action
                putExtra(ID, id.toString())
            }
            context.startForegroundService(intent)
        }
    }

    private fun startSync(id: UUID) {
        Thread {
            runBlocking {
                val shopId = shopRepository.get()?.id
                val token = sanctumRepository.getSyncToken()
                val machineUsage = machineRepository.getMachineUsage(id)

                if(token == null) {
                    println("No token")
                    safeStop()
                    return@runBlocking
                }

                if(shopId == null) {
                    return@runBlocking
                }

                if(machineUsage == null) {
                    println("Payment cannot be null")
                    return@runBlocking
                }

                try {
                    networkRepository.sendMachineUsage(machineUsage, shopId, token).let {
                        safeStop()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showNotification(UPT_SYNC_NOTIFICATION_ID,"Failed to sync machine usage", e.message.toString())
                    safeStop(10)
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
        startForeground(SVC_SYNC_NOTIFICATION_ID, getNotification("Sync started", "Updating machine usages data."))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}