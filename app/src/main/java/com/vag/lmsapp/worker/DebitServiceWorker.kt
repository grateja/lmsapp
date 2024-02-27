//package com.csi.palabakosys.worker
//
//import android.content.Context
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import androidx.work.workDataOf
//import com.csi.palabakosys.room.entities.EntityActivationRef
//import com.csi.palabakosys.room.entities.EntityJobOrderService
//import com.csi.palabakosys.room.entities.EntityMachine
//import com.csi.palabakosys.room.entities.EntityMachineUsage
//import com.csi.palabakosys.room.repository.JobOrderQueuesRepository
//import com.csi.palabakosys.room.repository.MachineRepository
//import com.csi.palabakosys.room.repository.RemoteRepository
//import com.google.gson.Gson
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//import java.time.Instant
//import java.util.*
//
//@HiltWorker
//class DebitServiceWorker
//@AssistedInject
//constructor (
//    @Assisted context: Context,
//    @Assisted workerParams: WorkerParameters,
//    private val remoteRepository: RemoteRepository,
//    private val queuesRepository: JobOrderQueuesRepository,
//) : CoroutineWorker(context, workerParams) {
//    companion object {
//        const val MESSAGE = "message"
//        const val MACHINE_ID = "machineId"
//        const val JOB_ORDER_SERVICE_ID = "jobOrderServiceId"
////        const val MACHINE_USAGE = "machineUsage"
////        const val ACTIVATION_REF = "activationRef"
//    }
//    var message = ""
//    override suspend fun doWork(): Result {
//        val gson = Gson()
//        val machineId = inputData.getString(MACHINE_ID)
//        val jobOrderServiceId = inputData.getString(JOB_ORDER_SERVICE_ID)
////        val activationRef = gson.fromJson(inputData.getString(ACTIVATION_REF), EntityActivationRef::class.java)
////        val machineUsage = gson.fromJson(inputData.getString(MACHINE_USAGE), EntityMachineUsage::class.java)
//
//        val jobOrderService = queuesRepository.get(jobOrderServiceId)
//            ?: return Result.failure(workDataOf(MESSAGE to message))
//
//        val machineUsage = EntityMachineUsage(UUID.fromString(machineId), UUID.fromString(jobOrderServiceId))
//
//        val activationRef = EntityActivationRef(Instant.now(), jobOrderService.serviceRef.minutes,  jobOrderService.jobOrderId)
//
//        return if(update(activationRef, jobOrderServiceId, machineId, machineUsage)) {
//            Result.success(workDataOf(MESSAGE to message))
//        } else {
//            Result.failure(workDataOf(MESSAGE to message))
//        }
//    }
//    suspend fun update(activationRef: EntityActivationRef, jobOrderServiceId: String?, machineId: String?, machineUsage: EntityMachineUsage) : Boolean {
//        return try {
//            println("id from debit service")
////            machine.workerId = id
//            remoteRepository.activate(activationRef, jobOrderServiceId, machineId, machineUsage)
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//}