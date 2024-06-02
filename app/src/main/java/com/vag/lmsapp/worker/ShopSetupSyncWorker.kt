package com.vag.lmsapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.room.repository.MachineRepository
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ShopSetupSyncWorker

@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository,
    private val shopRepository: ShopRepository,
    private val sanctumRepository: SanctumRepository,
    private val machineRepository: MachineRepository,
    private val serviceRepository: WashServiceRepository,
    private val productRepository: ProductRepository,
    private val extraRepository: ExtrasRepository,
    private val deliveryRepository: DeliveryProfilesRepository,
    private val discountRepository: DiscountsRepository,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {
    companion object {
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(ShopSetupSyncWorker::class.java)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync-shop",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val shop = shopRepository.get()
        val shopId = shop?.id
        val token = sanctumRepository.getSyncToken()

        val machines = machineRepository.unSynced()
        val services = serviceRepository.unSynced()
        val products = productRepository.unSynced()
        val extras = extraRepository.unSynced()
        val deliveryProfiles = deliveryRepository.unSynced()
        val discounts = discountRepository.unSynced()
        val staffs = userRepository.unSynced()

        if(shopId == null) {
            println("Shop id cannot be null")
            return Result.failure()
        } else {
            println("Shop id")
            println(shopId)
        }

        if(token == null) {
            println("No token")
            return Result.failure()
        } else {
            println("token")
            println(token)
        }

        val payload = SetupRequestBody(
            shop,
            machines,
            staffs,
            services,
            products,
            extras,
            deliveryProfiles,
            discounts
        )

        return networkRepository.sendBulkPayload(payload, shopId, token).let {
            return@let Result.success()
        }
    }
}