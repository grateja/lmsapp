package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoShop
import com.vag.lmsapp.room.entities.EntityShop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository
@Inject
constructor (
    private val daoShop: DaoShop,
) {
    suspend fun get() : EntityShop? {
        return daoShop.get()
    }

    suspend fun save(shop: EntityShop) {
        try {
            daoShop.save(shop)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}