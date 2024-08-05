package com.vag.lmsapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.model.MachineActivationQueues
import com.vag.lmsapp.model.MachineConnectionStatus
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.room.repository.*
import com.vag.lmsapp.settings.DeveloperSettingsRepository
import com.vag.lmsapp.util.MachineActivationBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MachineActivationService : Service() {
    @Inject
    lateinit var remoteRepository: RemoteRepository

    @Inject
    lateinit var machineRepository: MachineRepository

    @Inject
    lateinit var queuesRepository: JobOrderQueuesRepository

    @Inject
    lateinit var customerRepository: CustomerRepository

    @Inject
    lateinit var queues: MachineActivationBus

    @Inject
    lateinit var dataStoreRepository: DeveloperSettingsRepository

    companion object {
        const val MACHINE_ACTIVATION = "machine_activation"
        const val MACHINE_ACTIVATION_READY = "machine_activation_ready"
        const val INPUT_INVALID_ACTION = "input_invalid"
        const val DATABASE_INCONSISTENCIES_ACTION = "inconsistent_db"

        const val ACTIVATION_QUEUES_EXTRA = "pending_queues"
        const val MESSAGE_EXTRA = "message_extra"

        const val CHECK_ONLY_EXTRA = "check_only"
        const val JO_SERVICE_ID_EXTRA = "jo_service_id"
        const val CUSTOMER_ID_EXTRA = "customer_id"

        private const val NOTIFICATION_ID = 102
        private const val CHANNEL_ID = "IAmSuperAwesome"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification(null))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        val checkOnly = intent?.getBooleanExtra(CHECK_ONLY_EXTRA, false) ?: false
        val queue = intent?.getParcelableExtra<MachineActivationQueues>(ACTIVATION_QUEUES_EXTRA)

        if(checkOnly) {
            val pending = checkPendingQueues(queue?.machineId)
            if(pending != null) {
                // currently connecting
                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(MACHINE_ACTIVATION).apply {
                    putExtra(ACTIVATION_QUEUES_EXTRA, pending)
                })
            } else {
                checkInconsistencies(queue)
            }
        } else {
            enqueue(queue)
        }

        return START_NOT_STICKY
    }

    private fun checkInconsistencies(queues: MachineActivationQueues?) {
        val context = this
        Thread {
            runBlocking {
                val machine = machineRepository.get(queues?.machineId)
                if(machine?.serviceActivationId != null) {
                    // something is wrong
                    sendInvalidInput("Inconsistencies with the database detected")

                    queues?.status = MachineConnectionStatus.DATA_INCONSISTENT
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(DATABASE_INCONSISTENCIES_ACTION).apply {
                         putExtra(ACTIVATION_QUEUES_EXTRA, queues)
                    })
                } else {
                    // all good
                    // send empty queue
                    safeStop()
                    queues?.status = MachineConnectionStatus.READY
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(MACHINE_ACTIVATION_READY).apply {
                        putExtra(ACTIVATION_QUEUES_EXTRA, queues)
                    })
                }
            }
        }.start()
    }

    private fun enqueue(queues: MachineActivationQueues?) {
        println("enqueue")
        println(queues)
        if(queues?.machineId != null && queues.jobOrderServiceId != null && queues.customerId != null) {
            start(queues)
        } else {
            sendInvalidInput("Inconsistent data detected")
        }
    }

    private fun checkPendingQueues(machineId: UUID?) : MachineActivationQueues? {
        return queues.get(machineId)
    }

    private fun finishQueue(machineId: UUID, status: MachineConnectionStatus, message: String) {
        queues.get(machineId)?.apply {
            this.status = status
            this.message = message
        }?.let { queue ->
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(MACHINE_ACTIVATION).apply {
                putExtra(ACTIVATION_QUEUES_EXTRA, queue)
            })

            val notification = getNotification(message)
            startForeground(NOTIFICATION_ID, notification)

            Thread {
                runBlocking {
                    delay(1000L)
                    queues.remove(queue.machineId)
                    safeStop()
                }
            }.start()
        }
    }

    private fun safeStop() {
        if(queues.size() == 0) {
            stopSelf()
        }
    }

    private fun getNotification(message: String?): Notification {
//        val message = if(message == null) "Establishing connection" else "Connecting to $message..."
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            // 2
            .setContentTitle(this.getString(R.string.app_name))
            .setContentText(message)
//            .setSound(null)
//            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            // 3
            .setAutoCancel(true)

        notificationManager.createNotificationChannel(createChannel())
        return builder.build()
    }

//    private fun getNotification(): Notification {
//        notificationManager.createNotificationChannel(createChannel())
//        return notificationBuilder.build()
//    }
//
    private fun createChannel() =
        NotificationChannel(
            CHANNEL_ID,
            "CHANNEL_NAME",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "CHANNEL_DESCRIPTION"
            setSound(null, null)
        }

    private val notificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private suspend fun client(): OkHttpClient {
        val timeout = dataStoreRepository.getTimeout()
        return OkHttpClient.Builder()
            .cache(null)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    private fun start(queue: MachineActivationQueues) {
        val context = this

        Thread {
            runBlocking {
                val machine = machineRepository.get(queue.machineId)
                val jobOrderService = queuesRepository.get(queue.jobOrderServiceId)
                val customer = customerRepository.get(queue.customerId)

                if (validate(machine, jobOrderService, customer)) {

                    queue.message = "Connecting to ${machine?.machineName()}..."
                    queue.status = MachineConnectionStatus.CONNECTING

                    queues.add(queue)

                    startForeground(NOTIFICATION_ID, getNotification("Connecting to ${machine?.machineName()}..."))

                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(MACHINE_ACTIVATION).apply {
                        putExtra(ACTIVATION_QUEUES_EXTRA, queue)
                    })

                    remoteRepository.preActivate(machine!!.id, jobOrderService!!.id, customer!!.id)

                    if (connect(machine, jobOrderService)) {

                        val machineUsage = EntityMachineUsage(machine.id, jobOrderService.id, customer.id)
                        val activationRef = EntityActivationRef(
                            Instant.now(),
                            jobOrderService.serviceRef.minutes,
                            jobOrderService.jobOrderId,
                            customer.id,
                            machineUsage.id
                        )

                        remoteRepository.activate(
                            activationRef,
                            jobOrderService.id,
                            machine.id,
                            machineUsage
                        )

                        MachineUsageSyncService.start(context, machineUsage.id)

                    } else {
                        remoteRepository.revertActivation(machine.id, jobOrderService.id)
                    }
                }
            }
        }.start()
    }

    private fun sendInvalidInput(message: String) {
        val intent = Intent(INPUT_INVALID_ACTION)
        intent.putExtra(MESSAGE_EXTRA, message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        safeStop()
    }

    private fun validate(machine: EntityMachine?, jobOrderService: EntityJobOrderService?, customer: EntityCustomer?) : Boolean {
        if(machine == null || (machine.ipEnd <= 1 || machine.ipEnd >= 254)) {
            sendInvalidInput("Invalid IP Address")
            return false
        }

        if((machine.activationRef?.remainingTime() ?: 0) > 0) {
            sendInvalidInput("Machine is already running")
            return false
        }

        if(jobOrderService == null) {
            sendInvalidInput("Invalid Service")
            return false
        }

        if(jobOrderService.serviceRef.pulse() <= 0) {
            sendInvalidInput("Pulse cannot be 0")
            return false
        }

        if(jobOrderService.quantity <= jobOrderService.used) {
            sendInvalidInput(jobOrderService.serviceName + " already used")
            return false
        }

        if(customer?.name == null) {
            sendInvalidInput("Customer must have a name")
            return false
        }

        return true
    }

    private suspend fun connect(machine: EntityMachine, jobOrderService: EntityJobOrderService) : Boolean {
        val fakeConnectOn = dataStoreRepository.getFakeConnectionModeOn()
        if(fakeConnectOn) {
            val fakeDelay = dataStoreRepository.getFakeConnectionDelay()
            delay(fakeDelay)
            finishQueue(machine.id, MachineConnectionStatus.SUCCESS, "${machine.machineName()} Test Activation success")
            return true
        }

        val token = "${jobOrderService.id}-${(jobOrderService.quantity - jobOrderService.used)}"
        val pulse = jobOrderService.serviceRef.pulse()

        val endpoint = dataStoreRepository.getEndpoint()
        val prefix = dataStoreRepository.getPrefix()
        val subnet = dataStoreRepository.getSubnet()
        val ipAddress = "$prefix.$subnet.${machine.ipEnd}" //appPreferences.ipSettings.toString(machine.ipEnd)
        val url = "http://$ipAddress/$endpoint" //appPreferences.urlSettings.toString(ipAddress)
        val serviceType = machine.serviceType?.value ?: "Wash"

        val requestBody = FormBody.Builder()
            .add("pulse", pulse.toString())
            .add("token", token)
            .add("serviceType", serviceType)
            .build()

        println(url)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .tag(machine.id)
            .build()

        return try {
            val response = client().newCall(request).execute()//.body()?.string().toString()
            val body = response.body()?.string().toString()

            if(response.code() == 200) {
                if(body.toInt() < 1) {
                    finishQueue(machine.id, MachineConnectionStatus.FAILED,"Invalid response from card terminal")
                    return false
                } else {
                    finishQueue(machine.id, MachineConnectionStatus.SUCCESS, "${machine.machineName()} Activation success")
                    true
                }
            } else {
                finishQueue(machine.id, MachineConnectionStatus.FAILED,"Invalid response from card terminal")
                return false
            }
        } catch (e: java.net.ConnectException) {
            finishQueue(machine.id, MachineConnectionStatus.FAILED, "Failed to connect to card terminal")
            return false
        } catch (e: java.net.SocketTimeoutException) {
            finishQueue(machine.id, MachineConnectionStatus.FAILED, "Connection to card terminal timed out")
            return false
        } catch(e: NumberFormatException) {
            finishQueue(machine.id, MachineConnectionStatus.FAILED, "Request success but got an invalid response from the terminal")
            return false
        } catch (e: Exception) {
            finishQueue(machine.id, MachineConnectionStatus.FAILED, e.message.toString())
            e.printStackTrace()
            false
        }
    }
}