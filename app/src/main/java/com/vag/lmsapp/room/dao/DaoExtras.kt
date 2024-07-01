package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.room.entities.EntityExtras
import java.util.UUID

@Dao
abstract class DaoExtras : BaseDao<EntityExtras> {

    @Query("""
        SELECT * 
        FROM extras 
        WHERE id = :id AND deleted = 0
    """)
    abstract suspend fun get(id: UUID): EntityExtras?

    @Query("""
        SELECT *, 1 as quantity, 0 as void, 0 as discounted_price 
        FROM extras 
        WHERE deleted = 0 
        ORDER BY name
    """)
    abstract suspend fun menuItems(): List<MenuExtrasItem>

    @Query("""
        SELECT *, 1 as quantity 
        FROM extras 
        WHERE name LIKE '%' || :keyword || '%' AND deleted = 0 
        ORDER BY name
    """)
    abstract suspend fun filter(keyword: String): List<ExtrasItemFull>

    @Query("""
        SELECT DISTINCT category 
        FROM extras 
        WHERE category IS NOT NULL 
        ORDER BY category
    """)
    abstract fun getCategories(): LiveData<List<String>>

    @Query("""
        SELECT * 
        FROM extras 
        WHERE sync = 0 OR :forced
    """)
    abstract suspend fun unSynced(forced: Boolean): List<EntityExtras>
}
