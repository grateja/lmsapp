package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.room.entities.EntityShop

@Dao
abstract class DaoShop : BaseDao<EntityShop> {
    @Query("SELECT * FROM shops LIMIT 1")
    abstract suspend fun get() : EntityShop?

    @Query("SELECT * FROM shops LIMIT 1")
    abstract fun getAsLiveData(): LiveData<EntityShop>
}