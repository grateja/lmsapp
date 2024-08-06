package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.dao.DaoMachine
import com.vag.lmsapp.room.entities.EntityMachine
import com.vag.lmsapp.room.entities.EntityMachineRemarks
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MachineRepository
@Inject constructor (
    private val daoMachine: DaoMachine,
) : BaseRepository<EntityMachine>(daoMachine) {
    override suspend fun get(id: UUID?) : EntityMachine? {
        if(id == null) return null
        return daoMachine.get(id)
    }

    suspend fun getAll(machineType: EnumMachineType, serviceType: EnumServiceType): List<EntityMachine> {
        return daoMachine.getAll(machineType, serviceType)
    }

    suspend fun getLastStackOrder(filter: MachineTypeFilter?) : Int {
        return daoMachine.getLastStackOrder(filter?.machineType, filter?.serviceType)?:1
    }

//    suspend fun setWorkerId(machineId: UUID, workerId: UUID?) {
//        daoMachine.setWorkerId(machineId, workerId)
//    }

    fun getAllAsLiveData() = daoMachine.getAllAsLiveData()

    fun getListAsLiveData(filter: MachineTypeFilter) = daoMachine.getListAllAsLiveData(filter.machineType, filter.serviceType)

    fun getMachineLiveData(id: UUID?) = daoMachine.getMachineLiveData(id)

//    fun getDashboard(dateFilter: DateFilter) = daoMachine.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)

    suspend fun getMachineUsage(machineId: UUID?, machineTypeFilter: MachineTypeFilter, keyword: String?, page: Int, dateFilter: DateFilter?): List<EntityMachineUsageDetails> {
        val offset = (20 * page) - 20
        return daoMachine.getMachineUsage(machineId, machineTypeFilter.machineType, machineTypeFilter.serviceType, keyword, offset, dateFilter?.dateFrom, dateFilter?.dateTo)
    }

    suspend fun unSynced(forced: Boolean) = daoMachine.unSynced(forced)

    suspend fun getMachineUsage(machineUsageId: UUID) = daoMachine.getMachineUsage(machineUsageId)
    fun getMachineUsageAsLiveData(id: UUID?) = daoMachine.getMachineUsageAsLiveData(id)

    suspend fun addRemarks(machineRemarks: EntityMachineRemarks) = daoMachine.addRemarks(machineRemarks)

    suspend fun rearrange(machines: List<MachineListItem>) = daoMachine.rearrange(machines)
}