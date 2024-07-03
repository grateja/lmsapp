package com.vag.lmsapp.room.repository

import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.dao.DaoJobOrderQueues
import com.vag.lmsapp.room.entities.EntityAvailableService
import com.vag.lmsapp.room.entities.EntityJobOrderService
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderQueuesRepository
@Inject
constructor (
    private val dao: DaoJobOrderQueues,
) {
//    suspend fun getByMachineType(machineType: EnumMachineType) : List<EntityCustomerQueueService> {
//        try {
//            return dao.getCustomersByMachineType(machineType)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return emptyList()
//    }
    fun getByMachineType(filter: MachineTypeFilter?) = dao.getCustomersByMachineType(filter?.machineType, filter?.serviceType)

    suspend fun getAvailableServiceByCustomerId(customerId: UUID, filter: MachineTypeFilter) : List<EntityAvailableService> {
        try {
            return dao.getAvailableWashes(customerId, filter.machineType, filter.serviceType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun getAvailableServicesByCustomerIdAsLiveData(customerId: UUID, filter: MachineTypeFilter) = dao.getAvailableServicesAsLiveData(customerId, filter.machineType, filter.serviceType)

    fun getAsLiveData(id: UUID?) = dao.getAsLiveData(id)

    suspend fun get(id: UUID?) : EntityJobOrderService? {
        return dao.get(id)
    }
}