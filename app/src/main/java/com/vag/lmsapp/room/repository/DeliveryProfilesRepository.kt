package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.delivery.MenuDeliveryProfile
import com.vag.lmsapp.room.dao.DaoDeliveryProfile
import com.vag.lmsapp.room.entities.EntityDeliveryProfile
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryProfilesRepository
@Inject
constructor (
    private val daoDeliveryProfile: DaoDeliveryProfile,
) : BaseRepository<EntityDeliveryProfile>(daoDeliveryProfile) {
    override suspend fun get(id: UUID?) : EntityDeliveryProfile? {
        if(id == null) return null
        return daoDeliveryProfile.get(id)
    }

    suspend fun getAll() : List<MenuDeliveryProfile> {
        return daoDeliveryProfile.getAll()
    }
}