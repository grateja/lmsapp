package com.vag.lmsapp.network

import com.vag.lmsapp.network.dao.BranchDao
import com.vag.lmsapp.network.link.ShopRequestBody
import com.vag.lmsapp.room.entities.EntityShop
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository

@Inject
constructor(
    private val dao: BranchDao
) {
    suspend fun link(shop: EntityShop, ownerId: UUID): Response<String> {
        println("link")
        return dao.link(ShopRequestBody.fromShopEntity(shop), ownerId.toString())
    }
}