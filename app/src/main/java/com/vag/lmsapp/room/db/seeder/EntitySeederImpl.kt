package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.room.dao.BaseDao
import com.vag.lmsapp.room.entities.BaseEntity

abstract class EntitySeederImpl<T: BaseEntity>(val dao: BaseDao<T>) : EntitySeederInterface<T> {
    override suspend fun seed(): List<T> {
        val items = items()
        dao.saveAll(items)
        return items
    }
}