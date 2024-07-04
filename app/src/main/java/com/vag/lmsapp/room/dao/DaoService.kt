package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.app.services.preview.ServicePreview
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.entities.EntityService
import java.util.UUID

@Dao
abstract class DaoService : BaseDao<EntityService> {
    @Query("SELECT * FROM services WHERE id = :id")
    abstract suspend fun get(id: UUID) : EntityService?

    @Query("SELECT * FROM services")
    abstract suspend fun getAll() : List<EntityService>

    @Query("SELECT *, 1 as quantity, 0 as used, 0 as void, 0 AS discounted_price FROM services WHERE deleted = 0")
    abstract suspend fun menuItems() : List<MenuServiceItem>

    @Query("SELECT * FROM services WHERE svc_machine_type = :machineType AND svc_service_type = :serviceType AND deleted = 0")
    abstract fun getByMachineTypeAsLiveData(machineType: EnumMachineType?, serviceType: EnumServiceType?) : LiveData<List<EntityService>>

    @Query("SELECT * FROM services WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityService>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    abstract fun getPreviewAsLiveData(serviceId: UUID): LiveData<ServicePreview?>

    @Query("UPDATE services SET hidden = CASE WHEN hidden = 1 THEN 0 ELSE 1 END WHERE id = :serviceId")
    abstract suspend fun hideToggle(serviceId: UUID)
}