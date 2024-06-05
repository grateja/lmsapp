package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.joborders.create.discount.MenuDiscount
import com.vag.lmsapp.room.entities.EntityDiscount
import java.util.UUID

@Dao
abstract class DaoDiscount : BaseDao<EntityDiscount> {
    @Query("SELECT * FROM discounts WHERE id = :id AND deleted = 0")
    abstract suspend fun get(id: UUID) : EntityDiscount?

    @Query("SELECT *, 0 as void FROM discounts WHERE name LIKE '%' || :keyword || '%' AND deleted = 0 ORDER BY name")
    abstract suspend fun getAll(keyword: String) : List<MenuDiscount>

    @Query("SELECT * FROM discounts WHERE name LIKE '%' || :keyword || '%' AND deleted = 0 ORDER BY name")
    abstract suspend fun filter(keyword: String): List<EntityDiscount>

    @Query("SELECT * FROM discounts WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityDiscount>
}