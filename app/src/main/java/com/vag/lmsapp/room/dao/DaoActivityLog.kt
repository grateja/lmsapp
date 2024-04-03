package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.activity_log.ActivityLogItemFull
import com.vag.lmsapp.room.entities.EntityActivityLog
import java.util.UUID

@Dao
abstract class DaoActivityLog : BaseDao<EntityActivityLog> {
    @Query("SELECT * FROM activity_logs WHERE id = :id AND deleted_at IS NULL")
    abstract suspend fun get(id: UUID) : EntityActivityLog?

    @Query("SELECT *, 1 as quantity FROM  activity_logs WHERE remarks LIKE '%' || :keyword || '%' AND deleted_at IS NULL ORDER BY created_at")
    abstract suspend fun filter(keyword: String) : List<ActivityLogItemFull>
}