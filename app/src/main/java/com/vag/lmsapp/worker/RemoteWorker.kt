//package com.csi.palabakosys.worker
//
//import android.content.Context
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.Data
//import androidx.work.WorkerParameters
//import androidx.work.workDataOf
//import com.csi.palabakosys.preferences.AppPreferenceRepository
//import com.csi.palabakosys.room.entities.EntityActivationRef
//import com.csi.palabakosys.room.entities.EntityJobOrderService
//import com.csi.palabakosys.room.entities.EntityMachine
//import com.csi.palabakosys.room.entities.EntityMachineUsage
//import com.csi.palabakosys.room.repository.JobOrderQueuesRepository
//import com.csi.palabakosys.room.repository.MachineRepository
//import com.csi.palabakosys.room.repository.RemoteRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//import okhttp3.*
//import java.security.DigestInputStream
//import java.security.MessageDigest
//import java.time.Instant
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//@HiltWorker
//class RemoteWorker
//@AssistedInject
//constructor (
//    @Assisted context: Context,
//    @Assisted workerParams: WorkerParameters,
//    private val remoteRepository: RemoteRepository,
//    private val queuesRepository: JobOrderQueuesRepository,
//    private val machineRepository: MachineRepository,
//    private val appPreferences: AppPreferenceRepository
//) : CoroutineWorker(context, workerParams) {
//    companion object {
//        const val MACHINE_ID = "machineId"
//        const val MESSAGE = "message"
//        const val JOB_ORDER_SERVICE_ID = "jobOrderServiceId"
//        const val RESPONSE_CODE = "errorCode"
//        const val RESPONSE_BODY = "responseBody"
//    }
//    private val outputData = Data.Builder()
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(appPreferences.urlSettings.connectionTimeout, TimeUnit.SECONDS)
//        .writeTimeout(0, TimeUnit.SECONDS)
//        .readTimeout(0, TimeUnit.SECONDS)
//        .retryOnConnectionFailure(false)
//        .build()
//
//    override suspend fun doWork(): Result {
//        println("Work started")
//        val machineId = UUID.fromString(inputData.getString(MACHINE_ID))
//        val jobOrderServiceId = UUID.fromString(inputData.getString(JOB_ORDER_SERVICE_ID))
//
//        val machine = machineRepository.get(machineId)
//        if(machine == null) {
//            outputData.putString(MESSAGE, "Invalid machine ID")
//            return Result.failure(outputData.build())
//        } else {
//            machineRepository.setWorkerId(machine.id, id)
//            outputData.putString(MACHINE_ID, machine.id.toString())
//        }
//
//        val jobOrderService = queuesRepository.get(jobOrderServiceId)
//        if(jobOrderService == null) {
//            machineRepository.setWorkerId(machine.id, null)
//            return Result.failure(outputData.putString(MESSAGE, "Service not found").build())
//        }
//
//        return if(activate(machine, jobOrderService)) {
//            val machineUsage = EntityMachineUsage(machineId, jobOrderServiceId)
//            val activationRef = EntityActivationRef(Instant.now(), jobOrderService.serviceRef.minutes,  jobOrderService.jobOrderId)
//
//            remoteRepository.activate(activationRef, jobOrderServiceId, machineId, machineUsage)
//            Result.success(outputData.putString(MESSAGE, "Success").build())
//        } else {
//            machineRepository.setWorkerId(machine.id, null)
//            Result.failure(outputData.build())
//        }
//    }
//
//    private fun activate(machine: EntityMachine, jobOrderService: EntityJobOrderService) : Boolean {
//        if(machine.ipEnd <= 1 || machine.ipEnd >= 254) {
//            outputData.putString(MESSAGE, "Invalid IP Address")
//            return false
//        }
//
//        val token = "${jobOrderService.id}-${(jobOrderService.quantity - jobOrderService.used)}"
//        val pulse = jobOrderService.serviceRef.pulse()
//
//        if(pulse <= 0) {
//            outputData.putString(MESSAGE, "Pulse cannot be 0")
//            return false
//        }
//
//        if(jobOrderService.quantity <= jobOrderService.used) {
//            outputData.putString(MESSAGE, jobOrderService.serviceName + " already used")
//            return false
//        }
//
//        val ipAddress = appPreferences.ipSettings.toString(machine.ipEnd)
//        val url = appPreferences.urlSettings.toString(ipAddress)
//
//        val requestBody = FormBody.Builder()
//            .add("pulse", pulse.toString())
//            .add("token", token)
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        println(url)
//
//        return try {
//            val response = client.newCall(request).execute()//.body()?.string().toString()
//            val body = response.body()?.string().toString()
//
//            outputData
//                .putString(RESPONSE_BODY, body)
//                .putInt(RESPONSE_CODE, response.code())
//
//            if(response.code() == 200) {
//                if(body.toInt() < 1) {
//                    outputData.putString(MESSAGE, "Invalid response from card terminal")
//                    return false
//                } else {
//                    outputData.putString(MESSAGE, "Request Success")
//                    true
//                }
//            } else {
//                outputData.putString(MESSAGE, "Failed with status code " + response.code() + "/" + body)
//                return false
//            }
//        } catch(e: NumberFormatException) {
//            outputData.putString(MESSAGE, "Request success but got an invalid response from the terminal")
//            return false
//        } catch (e: Exception) {
//            outputData.putString(MESSAGE, e.message)
//            e.printStackTrace()
//            false
//        }
//    }
//}