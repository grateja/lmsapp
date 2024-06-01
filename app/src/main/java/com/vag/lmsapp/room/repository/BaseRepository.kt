package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.BaseDao
import com.vag.lmsapp.room.entities.BaseEntity
import java.time.Instant
import java.util.UUID

abstract class BaseRepository<Entity : BaseEntity>(
    private val crudDao: BaseDao<Entity>
) : IRepository<Entity> {
    override suspend fun save(entity: Entity) : Entity? {
        try {
            entity.updatedAt = Instant.now()
            entity.sync = false
            crudDao.save(entity)
            return entity
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override suspend fun delete(entity: Entity, permanent: Boolean) : Boolean {
        try {
            if(permanent) {
                crudDao.deletePermanent(entity)
            } else {
                crudDao.softDelete(entity)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}