package com.vag.lmsapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class BacklogSyncWorker

@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository,
    private val sanctumRepository: SanctumRepository,
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(BacklogSyncWorker::class.java)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync-backlog",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

//    suspend fun syncJobOrder(shopId: UUID, token: String) : Boolean {
//        val jobOrder = networkRepository.getUnSyncJobOrder()
//        if(jobOrder == null) {
//            // all done
//            return true
//        } else {
//            networkRepository.sendJobOrder(jobOrder, shopId, token)
//            syncJobOrder(shopId, token)
//            return false
//        }
//    }

    private suspend fun syncJobOrder(shopId: UUID, token: String): Boolean {
        try {
            while (true) {
                println("get job order")
                val jobOrder = networkRepository.getUnSyncJobOrder()
                println(jobOrder)
                if (jobOrder == null) {
                    // all done
                    return true
                } else {
                    println("syncing job order ${jobOrder.jobOrder.jobOrderNumber}")
                    val result = networkRepository.sendJobOrder(jobOrder, shopId, token)
                    println("result")
                    println(result)
                    if(result.isFailure) {
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exceptions such as network errors or unexpected issues
            println("An error occurred during job order synchronization: ${e.message}")
            return false
        }
    }

    override suspend fun doWork(): Result {
        val shopId = shopRepository.get()?.id
        val token = sanctumRepository.getSyncToken()

        println("Sync started")
        syncJobOrder(shopId!!, token!!)
        println("sync ended")

        return Result.success()
    }
}