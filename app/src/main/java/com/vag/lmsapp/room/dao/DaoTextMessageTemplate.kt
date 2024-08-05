package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.room.entities.EntityTextMessageTemplate
import java.util.UUID

@Dao
abstract class DaoTextMessageTemplate : BaseDao<EntityTextMessageTemplate> {
    @Query("SELECT * FROM text_message_templates WHERE id = :id AND deleted = 0")
    abstract suspend fun get(id: UUID) : EntityTextMessageTemplate?

    @Query("""
        SELECT * 
        FROM text_message_templates 
        WHERE title LIKE '%' || :keyword || '%' 
            AND deleted = 0 
        ORDER BY title
        LIMIT 20 OFFSET :offset
    """)
    abstract suspend fun filter(keyword: String, offset: Int): List<EntityTextMessageTemplate>

    @Query("SELECT * FROM text_message_templates WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityTextMessageTemplate>
}