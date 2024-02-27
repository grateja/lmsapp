package com.vag.lmsapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.model.EnumPrintState
import com.vag.lmsapp.settings.PrinterSettingsRepository
//import com.csi.palabakosys.room.repository.DataStoreRepository
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PrinterService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 103
        private const val CHANNEL_ID = "printer_settings_channel_id"
        private const val CHANNEL_NAME = "Printer Settings"
        private const val CHANNEL_DESCRIPTION = "Channel for printer settings notifications"

//        const val PRINTER_ADDRESS = "printerAddress"
//        const val PRINTER_WIDTH = "printerWidth"
//        const val PRINTER_CHARACTERS_PER_LINE = "charactersPerLine"

        const val PAYLOAD_TEXT = "payload"
        const val PRINT_STATE = "state"
//        const val PRINTER_SETTINGS = "settings"
//        const val PRINT_STARTED_ACTION = "print_started_action"
//        const val PRINT_FINISHED_ACTION = "print_finished_action"
        const val CANCEL_PRINT_ACTION = "print_finished_action"
//        const val PRINT_ERROR_ACTION = "print_error"
        const val PRINT_ACTION = "printAction"
        const val MESSAGE = "message"
    }

//    @Inject
//    lateinit var appPreferenceRepository: AppPreferenceRepository

    @Inject
    lateinit var dataStoreRepository: PrinterSettingsRepository

    var printer: EscPosPrinter? = null
    private var thread: Thread? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val action = intent?.action
        if(action == CANCEL_PRINT_ACTION) {
            cancel()
        }

//        val settings = intent?.getParcelableExtra<PrinterSettings>(PRINTER_SETTINGS)

        intent?.getStringExtra(PAYLOAD_TEXT)?.let {
//            val address = intent.getStringExtra(PRINTER_ADDRESS) ?: ""
//            val width = intent.getFloatExtra(PRINTER_WIDTH, 58f)
//            val charactersPerLine = intent.getIntExtra(PRINTER_CHARACTERS_PER_LINE, 32)

            startPrint(it)
        }

        return START_NOT_STICKY
    }

    private fun cancel() {
        thread?.interrupt()
        val intent = Intent(PRINT_ACTION).apply {
            putExtra(PRINT_STATE, EnumPrintState.CANCELED as Parcelable)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        stopSelf()
        println("canceled")
    }

    private val notificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(this.getString(R.string.app_name))
            .setContentText("Print started")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
    }

    private fun getNotification(): Notification {
        notificationManager.createNotificationChannel(createChannel())
        return notificationBuilder.build()
    }

    private fun createChannel() =
        NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
            setSound(null, null)
        }

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private fun startPrint(text: String) {
        val context = this
        thread = Thread {
            runBlocking {
                try {

                    val intent = Intent(PRINT_ACTION).apply {
                        putExtra(PRINT_STATE, EnumPrintState.STARTED as Parcelable)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                    val address = dataStoreRepository.getPrinterAddress()
                    val device = bluetoothAdapter.getRemoteDevice(address)
                    val connection = BluetoothConnection(device)

                    val width = dataStoreRepository.getPrinterWidth()
                    val characterLength = dataStoreRepository.getPrinterCharacters()

                    println("create printer")
                    printer = EscPosPrinter(connection, 203, width, characterLength)

                    if(Thread.interrupted()) {
                        return@runBlocking
                    }

                    println("start print")
                    printer?.printFormattedTextAndCut(text)

                    println("disconnect")
                    printer?.disconnectPrinter()

                } catch (e: Exception) {
                    e.printStackTrace()
                    val intent = Intent(PRINT_ACTION).apply {
                        putExtra(PRINT_STATE, EnumPrintState.ERROR as Parcelable)
                        putExtra(MESSAGE, e.message)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                } finally {
                    printer?.disconnectPrinter()
                    val intent = Intent(PRINT_ACTION).apply {
                        putExtra(PRINT_STATE, EnumPrintState.FINISHED as Parcelable)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    safeStop()
                }
            }
        }
        thread?.start()
    }

    private fun safeStop() {
        Thread {
            runBlocking {
                delay(3000L)
                stopSelf()
            }
        }.start()
    }
}