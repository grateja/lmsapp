package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.room.entities.EntityActivationRef
import com.vag.lmsapp.room.entities.EntityMachineRemarks
import com.vag.lmsapp.room.entities.EntityMachineUsage
import com.vag.lmsapp.room.entities.EntityRunningMachine
import java.time.Instant
import java.util.*

@Dao
interface DaoRemote {
    @Query("UPDATE machines SET time_activated = :timeActivated, total_minutes = :totalMinutes, service_activation_id = null, jo_service_id = :jobOrderServiceId, customer_id = :customerId, machine_usage_id = :machineUsageId WHERE id = :machineId")
    fun startMachine(machineId: UUID, jobOrderServiceId: UUID, customerId: UUID?, timeActivated: Instant?, totalMinutes: Int?, machineUsageId: UUID?)

    @Upsert
    fun insertMachineUsage(machineUsage: EntityMachineUsage)

    @Transaction
    suspend fun activate(activationRef: EntityActivationRef, jobOrderServiceId: UUID, machineId: UUID, machineUsage: EntityMachineUsage) {
        startMachine(machineId, jobOrderServiceId, activationRef.customerId, activationRef.timeActivated, activationRef.totalMinutes, activationRef.machineUsageId)
        insertMachineUsage(machineUsage)
    }

    @Query("UPDATE machines SET service_activation_id = :jobOrderServiceId WHERE id = :machineId")
    fun setServiceActivationId(machineId: UUID, jobOrderServiceId: UUID?)

    @Query("UPDATE machines SET customer_id = :customerId WHERE id = :machineId")
    fun preSetCustomer(machineId: UUID, customerId: UUID?)

    @Query("UPDATE job_order_services SET used = used + 1 WHERE id = :jobOrderServiceId")
    fun preUseService(jobOrderServiceId: UUID)

    @Query("UPDATE job_order_services SET used = used - 1 WHERE id = :jobOrderServiceId")
    fun revertUsage(jobOrderServiceId: UUID)

    @Transaction
    suspend fun preActivate(machineId: UUID, jobOrderServiceId: UUID, customerId: UUID) {
        setServiceActivationId(machineId, jobOrderServiceId)
        preUseService(jobOrderServiceId)
        preSetCustomer(machineId, customerId)
    }

    @Transaction
    suspend fun revertActivation(machineId: UUID, jobOrderServiceId: UUID) {
        setServiceActivationId(machineId, null)
        preSetCustomer(machineId, null)
        revertUsage(jobOrderServiceId)
    }

    @Transaction
    @Query("SELECT * FROM machines WHERE id = :machineId LIMIT 1")
    fun getActiveMachine(machineId: UUID?) : LiveData<EntityRunningMachine?>

    @Upsert
    fun insertMachineRemarks(machineRemarks: EntityMachineRemarks)

    @Query("UPDATE machines SET total_minutes = 0 WHERE id = :machineId")
    fun endMachine(machineId: UUID)

    @Transaction
    suspend fun endTime(machineRemarks: EntityMachineRemarks) {
        insertMachineRemarks(machineRemarks)
        endMachine(machineRemarks.machineId)
    }
}