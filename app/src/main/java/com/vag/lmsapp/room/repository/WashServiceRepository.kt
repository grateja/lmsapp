package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.dao.DaoService
import com.vag.lmsapp.room.entities.EntityService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WashServiceRepository
@Inject
constructor (
    private val daoWashService: DaoService,
) : BaseRepository<EntityService>(daoWashService) {
    override suspend fun get(id: UUID?) : EntityService? {
        if(id == null) return null
        return daoWashService.get(id)
    }

    suspend fun menuItems() : List<MenuServiceItem> {
        return daoWashService.menuItems()
    }

    fun getByMachineTypeAsLiveData(filter: MachineTypeFilter) =
        daoWashService.getByMachineTypeAsLiveData(filter.machineType, filter.serviceType)

    suspend fun unSynced(forced: Boolean) = daoWashService.unSynced(forced)

    fun getPreviewAsLiveData(serviceId: UUID) = daoWashService.getPreviewAsLiveData(serviceId)

    suspend fun hideToggle(serviceId: UUID) = daoWashService.hideToggle(serviceId)
}