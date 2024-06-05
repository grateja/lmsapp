package com.vag.lmsapp.room.repository

import com.vag.lmsapp.room.dao.DaoRemote
import com.vag.lmsapp.room.entities.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepository
@Inject
constructor (
    private val daoRemote: DaoRemote
) {
    suspend fun activate(
        activationRef: EntityActivationRef, jobOrderServiceId: UUID, machineId: UUID, machineUsage: EntityMachineUsage
    ) {
        daoRemote.activate(activationRef, jobOrderServiceId, machineId, machineUsage)
    }

    suspend fun preActivate(machineId: UUID, jobOrderServiceId: UUID, customerId: UUID) {
        daoRemote.preActivate(machineId, jobOrderServiceId, customerId)
    }

    suspend fun revertActivation(machineId: UUID, jobOrderServiceId: UUID) {
        daoRemote.revertActivation(machineId, jobOrderServiceId)
    }

    fun getRunningMachine(machineId: UUID?) = daoRemote.getActiveMachine(machineId)

    suspend fun endTime(remarks: String, machine: EntityMachine, userId: UUID) {
        val machineRemarks = EntityMachineRemarks(
            machine.id,
            userId,
            "END_TIME",
            remarks
        )
        daoRemote.endTime(machineRemarks)
    }
}