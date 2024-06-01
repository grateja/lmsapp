package com.vag.lmsapp.room.dao

import androidx.room.*
import com.vag.lmsapp.room.entities.BaseEntity

interface BaseDao<T : BaseEntity> {
    @Upsert
    suspend fun save(data: T)

    @Delete
    suspend fun deletePermanent(data: T)

    suspend fun softDelete(data: T) {
        data.deleted = true
        save(data)
    }

    @Upsert
    suspend fun saveAll(list: List<T>)
}