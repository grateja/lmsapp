package com.vag.lmsapp.network.api

import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.network.responses.InventoryLogSyncIds
import com.vag.lmsapp.network.responses.MachineUsageSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import com.vag.lmsapp.network.responses.UuidSyncId
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.entities.EntityExpenseFull
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.entities.EntityMachineUsageFull
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.entities.SanctumToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ApiService {
    @Headers("Accept: application/json")
    @POST("api/shop/link/{ownerId}")
    suspend fun link(
        @Body shop: EntityShop,
        @Path("ownerId") ownerId: String,
        @Header("Authorization") token: String
    ): Response<SanctumToken>

    @Headers("Accept: application/json")
    @POST("api/sync/job-order/{shopId}")
    suspend fun sendJobOrder(
        @Body jobOrderWithItems: List<EntityJobOrderWithItems>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<JobOrderSyncIds>

    @Headers("Accept: application/json")
    @POST("api/sync/payment/{shopId}")
    suspend fun sendPayment(
        @Body paymentRequestBody: List<PaymentRequestBody>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<PaymentSynIds>

    @Headers("Accept: application/json")
    @POST("api/sync/setup/{shopId}")
    suspend fun sendSetup(
        @Body setupRequestBody: SetupRequestBody,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<SetupSyncIds>

    @Headers("Accept: application/json")
    @POST("api/sync/customer/{shopId}")
    suspend fun sendCustomer(
        @Body customers: List<EntityCustomer>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<List<UUID>>

    @Headers("Accept: application/json")
    @POST("api/sync/machine-usage/{shopId}")
    suspend fun sendMachineActivation(
        @Body machineUsage: List<EntityMachineUsageFull>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<MachineUsageSyncIds>

    @Headers("Accept: application/json")
    @POST("api/sync/expense/{shopId}")
    suspend fun sendExpense(
        @Body expense: List<EntityExpenseFull>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<List<UUID>>

    @Headers("Accept: application/json")
    @POST("api/sync/inventory-log/{shopId}")
    suspend fun sendInventoryLog(
        @Body body: List<EntityInventoryLogFull>,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<InventoryLogSyncIds>

    @Headers("Accept: application/json")
    @POST("api/sync/all/{shopId}")
    suspend fun sendAll(
        @Body customer: EntityCustomer,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<UUID>


}