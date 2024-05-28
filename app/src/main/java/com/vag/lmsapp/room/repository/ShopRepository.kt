package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoShop
import com.vag.lmsapp.room.entities.EntityShop
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository
@Inject
constructor (
    private val daoShop: DaoShop,
) : BaseRepository<EntityShop>(daoShop) {
    suspend fun get() : EntityShop? {
        return daoShop.get()
    }

    override suspend fun save(entity: EntityShop): EntityShop? {
        try {
            daoShop.save(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return entity
    }

    fun getAsLiveData() = daoShop.getAsLiveData()
    override suspend fun get(id: UUID?): EntityShop? {
        return daoShop.get()
    }
}