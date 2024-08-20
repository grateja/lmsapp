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
import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
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
    private val userRepository: UserRepository,
    private val packageRepository: JobOrderPackageRepository
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val FORCED = "forced"
        fun enqueue(context: Context, force: Boolean = false) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(ShopSetupSyncWorker::class.java)
                .setConstraints(constraints)

            if(force) {
                workRequest.setInputData(
                    Data.Builder()
                        .putBoolean(FORCED, force)
                        .build()
                )
            }

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync-shop",
                ExistingWorkPolicy.REPLACE,
                workRequest.build()
            )
        }
    }

    override suspend fun doWork(): Result {
        val shop = shopRepository.get()
        val shopId = shop?.id
        val token = sanctumRepository.getSyncToken()
        val forced = inputData.getBoolean(FORCED, false)

        val machines = machineRepository.unSynced(forced)
        val services = serviceRepository.unSynced(forced)
        val products = productRepository.unSynced(forced)
        val extras = extraRepository.unSynced(forced)
        val deliveryProfiles = deliveryRepository.unSynced(forced)
        val discounts = discountRepository.unSynced(forced)
        val staffs = userRepository.unSynced(forced)
        val packages = packageRepository.unSynced(forced)

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
            discounts,
            packages
        )

        return networkRepository.sendBulkPayload(payload, shopId, token).let {
            return@let Result.success()
        }
    }
}