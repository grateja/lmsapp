package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.vag.lmsapp.R
import com.vag.lmsapp.app.lms_live.sync.SyncActivity
import com.vag.lmsapp.services.BacklogSyncService.Companion.SYNC_MESSAGE_EXTRA
import com.vag.lmsapp.services.BacklogSyncService.Companion.SYNC_NAME_EXTRA
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

abstract class SyncService(protected val name: String, private val descriptions: String): Service() {
    companion object {
        const val CHANNEL_ID = "sync_service"
        var running = false
    }

    private val notificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createChannel() =
        NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = descriptions
            setSound(null, null)
        }

    protected fun getNotification(title: String, text: String): Notification {
        val notificationIntent = Intent(this, SyncActivity::class.java).apply {
            putExtra(SYNC_NAME_EXTRA, name)
            putExtra(SYNC_MESSAGE_EXTRA, text)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)



        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.icon_sync)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.createNotificationChannel(createChannel())
        return builder.build()
    }

    protected fun safeStop(seconds: Long = 1) {
        Thread {
            runBlocking {
                delay(seconds * 1000)
                stopSelf()
            }
        }.start()
    }

}