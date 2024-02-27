package com.vag.lmsapp.room.repository

import java.util.UUID

interface IRepository<T> {
    suspend fun get(id: UUID?) : T?
    suspend fun delete(entity: T, permanent: Boolean = false) : Boolean
    suspend fun save(entity: T) : T?
}