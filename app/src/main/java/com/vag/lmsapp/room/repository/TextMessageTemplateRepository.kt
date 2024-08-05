package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoTextMessageTemplate
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TextMessageTemplateRepository
@Inject
constructor (
    private val dao: DaoTextMessageTemplate,
) : BaseRepository<EntityTextMessageTemplate>(dao) {
    override suspend fun get(id: UUID?) : EntityTextMessageTemplate? {
        if(id == null) return null
        return dao.get(id)
    }

    suspend fun filter(keyword: String, page: Int): List<EntityTextMessageTemplate> {
        val offset = (20 * page) - 20
        return dao.filter(keyword, offset)
    }

    suspend fun unSynced(forced: Boolean) = dao.unSynced(forced)
}