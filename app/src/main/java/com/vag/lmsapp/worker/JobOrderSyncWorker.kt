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
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.JOB_ORDER_ID
import com.vag.lmsapp.util.toUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class JobOrderSyncWorker

@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository,
    private val sanctumRepository: SanctumRepository,
    private val jobOrderRepository: JobOrderRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueue(context: Context, jobOrderId: UUID) {
            val jobOrderIdStr = jobOrderId.toString()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workData = Data.Builder()
                .putString(JOB_ORDER_ID, jobOrderIdStr)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(JobOrderSyncWorker::class.java)
                .setInputData(workData)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                jobOrderIdStr,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val shopId = shopRepository.get()?.id
        val token = sanctumRepository.getSyncToken()
        val jobOrder = inputData.getString(JOB_ORDER_ID).toUUID()?.let {
            jobOrderRepository.getJobOrderWithItems(it)
        }

        if(shopId == null) {
            println("Shop id cannot be null")
            return Result.failure()
        }

        if(token == null) {
            println("No token")
            return Result.failure()
        }

        if(jobOrder == null) {
            println("Job order may be deleted")
            return Result.failure()
        }

        return networkRepository.sendJobOrder(jobOrder, shopId, token).let {
            return@let Result.success()
        }
    }
}