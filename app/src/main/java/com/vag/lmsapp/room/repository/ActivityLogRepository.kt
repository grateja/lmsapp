package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.activity_log.ActivityLogItemFull
import com.vag.lmsapp.room.dao.DaoActivityLog
import com.vag.lmsapp.room.entities.EntityActivityLog
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepository
@Inject
constructor (
    private val dao: DaoActivityLog,
) : BaseRepository<EntityActivityLog>(dao) {
    override suspend fun get(id: UUID?) : EntityActivityLog? {
        if(id == null) return null
        return dao.get(id)
    }

    suspend fun filter(keyword: String): List<ActivityLogItemFull> {
        return dao.filter(keyword)
    }
}