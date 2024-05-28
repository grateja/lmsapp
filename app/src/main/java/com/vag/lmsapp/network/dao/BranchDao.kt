package com.vag.lmsapp.network.dao

import com.vag.lmsapp.network.BulkPayload
import com.vag.lmsapp.network.PaymentRequestBody
import com.vag.lmsapp.network.link.ShopRequestBody
import com.vag.lmsapp.room.entities.BaseEntity
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.entities.SanctumToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface BranchDao {
    @Headers("Accept: application/json")
    @POST("api/shop/link/{ownerId}")
    suspend fun link(
        @Body shop: ShopRequestBody,
        @Path("ownerId") ownerId: String,
        @Header("Authorization") token: String
    ): Response<SanctumToken>

    @Headers("Accept: application/json")
    @POST("api/sync/job-order/{shopId}")
    suspend fun sendJobOrder(
        @Body jobOrderWithItems: EntityJobOrderWithItems,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<String>

    @Headers("Accept: application/json")
    @POST("api/sync/payment/{shopId}")
    suspend fun sendPayment(
        @Body paymentRequestBody: PaymentRequestBody,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<String>

    @Headers("Accept: application/json")
    @POST("api/sync/bulk/{shopId}")
    suspend fun sendBulkPayload(
        @Body bulkPayload: BulkPayload,
        @Path("shopId") shopId: String,
        @Header("Authorization") token: String
    ): Response<String>
}