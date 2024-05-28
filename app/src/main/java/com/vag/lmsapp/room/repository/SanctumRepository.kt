package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoSanctum
import com.vag.lmsapp.room.dao.DaoShop
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.entities.SanctumToken
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SanctumRepository
@Inject
constructor (
    private val dao: DaoSanctum,
) {
    suspend fun getSyncToken() = dao.getSyncToken()

    suspend fun save(sanctumToken: SanctumToken) = dao.save(sanctumToken)
}