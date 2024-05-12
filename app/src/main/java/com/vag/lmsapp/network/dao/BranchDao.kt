package com.vag.lmsapp.network.dao

import com.vag.lmsapp.network.link.ShopRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface BranchDao {
    @POST("api/shop/link/{ownerId}")
    suspend fun link(@Body shop: ShopRequestBody, @Path("ownerId") ownerId: String): Response<String>
}