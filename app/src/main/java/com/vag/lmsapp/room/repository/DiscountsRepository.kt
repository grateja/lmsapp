package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.discount.MenuDiscount
import com.vag.lmsapp.room.dao.DaoDiscount
import com.vag.lmsapp.room.entities.EntityDiscount
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DiscountsRepository
@Inject
constructor (
    private val dao: DaoDiscount,
) : BaseRepository<EntityDiscount>(dao) {
    override suspend fun get(id: UUID?) : EntityDiscount? {
        if(id == null) return null
        return dao.get(id)
    }

    suspend fun getAll(keyword: String) : List<MenuDiscount> {
        return dao.getAll(keyword)
    }

    suspend fun filter(keyword: String): List<EntityDiscount> {
        return dao.filter(keyword)
    }
}