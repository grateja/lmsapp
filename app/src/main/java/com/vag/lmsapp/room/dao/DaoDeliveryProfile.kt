package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.joborders.create.delivery.MenuDeliveryProfile
import com.vag.lmsapp.room.entities.EntityDeliveryProfile
import java.util.*

@Dao
abstract class DaoDeliveryProfile : BaseDao<EntityDeliveryProfile> {
    @Query("SELECT * FROM delivery_profiles WHERE id = :id AND deleted = 0")
    abstract suspend fun get(id: UUID) : EntityDeliveryProfile?

    @Query("SELECT *, 0 as void FROM delivery_profiles WHERE deleted = 0")
    abstract suspend fun getAll() : List<MenuDeliveryProfile>

    @Query("SELECT * FROM delivery_profiles WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityDeliveryProfile>
}