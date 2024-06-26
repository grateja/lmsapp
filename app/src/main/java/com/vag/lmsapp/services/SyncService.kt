package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.vag.lmsapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

abstract class SyncService(private val name: String, private val descriptions: String): Service() {
    companion object {
        const val CHANNEL_ID = "sync_service"
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
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.icon_sync)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .setAutoCancel(true)

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