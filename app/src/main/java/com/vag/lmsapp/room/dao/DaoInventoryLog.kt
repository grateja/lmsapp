package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.room.entities.EntityInventoryLog
import java.util.UUID

@Dao
abstract class DaoInventoryLog : BaseDao<EntityInventoryLog> {
    @Query("SELECT * FROM inventory_log WHERE id = :id AND deleted_at IS NULL")
    abstract suspend fun get(id: UUID) : EntityInventoryLog?

    @Query("SELECT il.*, prd.name FROM inventory_log il JOIN products prd ON prd.id = il.product_id WHERE prd.name LIKE '%' || :keyword || '%' AND il.deleted_at IS NULL ORDER BY prd.name")
    abstract suspend fun getAll(keyword: String) : List<EntityInventoryLog>
}