package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.room.entities.BaseEntity

interface EntitySeederInterface<T: BaseEntity> {
    fun items() : List<T>
    suspend fun seed() : List<T>
}