package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.extras.list.ExtrasQueryResult
import com.vag.lmsapp.app.extras.list.advanced_filter.ExtrasAdvancedFilter
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.room.entities.EntityExtras
import com.vag.lmsapp.util.ResultCount
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
        SELECT *
        FROM extras 
        WHERE name LIKE '%' || :keyword || '%' AND deleted = 0 
        ORDER BY
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'ASC' THEN name END ASC,
            CASE WHEN :orderBy = 'Category' AND :sortDirection = 'ASC' THEN category END ASC,
            CASE WHEN :orderBy = 'Price' AND :sortDirection = 'ASC' THEN price END ASC,
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'DESC' THEN name END DESC,
            CASE WHEN :orderBy = 'Category' AND :sortDirection = 'DESC' THEN category END DESC,
            CASE WHEN :orderBy = 'Price' AND :sortDirection = 'DESC' THEN price END DESC
        LIMIT 20 OFFSET :offset
    """)
    abstract suspend fun filter(keyword: String, offset: Int, orderBy: String?, sortDirection: String?): List<ExtrasItemFull>

    suspend fun queryResult(keyword: String, offset: Int, advancedFilter: ExtrasAdvancedFilter): ExtrasQueryResult {
        return ExtrasQueryResult(
            filter(keyword, offset, advancedFilter.orderBy, advancedFilter.sortDirection.toString()),
            count(keyword)
        )
    }

    @Query("""SELECT
        (
            SELECT COUNT(*)
            FROM extras
            WHERE name LIKE '%' || :keyword || '%'
                AND deleted = 0
        ) AS filtered, (
            SELECT COUNT(*)
            FROM extras
            WHERE deleted = 0
        ) AS total
    """)
    abstract suspend fun count(keyword: String): ResultCount

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
