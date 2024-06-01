package com.vag.lmsapp.network.api

import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.entities.SanctumToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

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
        @Body jobOrderWithItems: EntityJobOrderWithItems,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<JobOrderSyncIds>

    @Headers("Accept: application/json")
    @POST("api/sync/payment/{shopId}")
    suspend fun sendPayment(
        @Body paymentRequestBody: PaymentRequestBody,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<PaymentSynIds>

    @Headers("Accept: application/json")
    @POST("api/sync/bulk/{shopId}")
    suspend fun sendBulkPayload(
        @Body setupRequestBody: SetupRequestBody,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<SetupSyncIds>
}