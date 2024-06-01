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
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.util.Constants.Companion.ID
import com.vag.lmsapp.util.toUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class JobOrderPaymentSyncWorker

@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository,
    private val sanctumRepository: SanctumRepository,
    private val paymentRepository: PaymentRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueue(context: Context, paymentId: UUID) {
            val paymentIdStr = paymentId.toString()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workData = Data.Builder()
                .putString(ID, paymentIdStr)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(JobOrderPaymentSyncWorker::class.java)
                .setInputData(workData)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                paymentIdStr,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val shopId = shopRepository.get()?.id
        val token = sanctumRepository.getSyncToken()
        val payment = inputData.getString(ID).toUUID()?.let {
            paymentRepository.getPaymentWithJobOrders(it)
        }

        if(shopId == null) {
            println("Shop id cannot be null")
            return Result.failure()
        }

        if(token == null) {
            println("No token")
            return Result.failure()
        }

        if(payment == null) {
            println("Job order may be deleted")
            return Result.failure()
        }

        val paymentRequestBody = PaymentRequestBody(
            payment.payment,
            payment.user,
            payment.jobOrders.map { it.id }
        )


        return networkRepository.sendPayment(paymentRequestBody, shopId, token).let {
            return@let Result.success()
        }
    }
}