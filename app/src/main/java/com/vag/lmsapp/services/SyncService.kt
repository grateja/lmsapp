package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat
import com.vag.lmsapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

abstract class SyncService(private val name: String, private val descriptions: String): Service() {
    companion object {
        const val CHANNEL_ID = "sync_service"
    }

    protected fun isNetworkAvailable() {

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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setAutoCancel(true)

        notificationManager.createNotificationChannel(createChannel())
        return builder.build()
    }

    protected fun safeStop() {
        Thread {
            runBlocking {
                delay(1000L)
                stopSelf()
            }
        }.start()
    }
}