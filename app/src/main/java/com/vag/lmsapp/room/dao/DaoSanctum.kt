package com.vag.lmsapp.room.dao

import androidx.room.*
import com.vag.lmsapp.room.entities.BaseEntity
import com.vag.lmsapp.room.entities.SanctumToken
import java.time.Instant

@Dao
interface DaoSanctum {
    @Upsert
    suspend fun save(data: SanctumToken)

    @Query("SELECT plainTextToken FROM sanctum_tokens WHERE name = 'shop-uploader' LIMIT 1")
    suspend fun getSyncToken(): String?
}