package com.vag.lmsapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vag.lmsapp.network.BulkPayload
import com.vag.lmsapp.network.NetworkRepository
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.room.repository.DiscountsRepository
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.room.repository.SanctumRepository
import com.vag.lmsapp.room.repository.ShopRepository
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LiveSyncWorker

@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkRepository: NetworkRepository,
    val shopRepository: ShopRepository,
    private val sanctumRepository: SanctumRepository,
    private val serviceRepository: WashServiceRepository,
    private val productRepository: ProductRepository,
    private val extraRepository: ExtrasRepository,
    private val deliveryRepository: DeliveryProfilesRepository,
    private val discountRepository: DiscountsRepository,
) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result {
            shopRepository.get().let {
                println("shop from worker")
                println(it)
            }
        val shopId = shopRepository.get()?.id
        val token = sanctumRepository.getSyncToken()

        val services = serviceRepository.unSynced()
        val products = productRepository.unSynced()
        val extras = extraRepository.unSynced()
        val deliveryProfiles = deliveryRepository.unSynced()
        val discounts = discountRepository.unSynced()

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

        val payload = BulkPayload(
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