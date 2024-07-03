package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.entities.EntityMachine
import com.vag.lmsapp.room.entities.EntityMachineRemarks
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.room.entities.EntityMachineUsageFull
import java.time.LocalDate
import java.util.*

@Dao
abstract class DaoMachine : BaseDao<EntityMachine> {
    @Query("SELECT * FROM machines WHERE machine_type = :machineType AND service_type = :serviceType ORDER BY stack_order")
    abstract suspend fun getAll(machineType: EnumMachineType, serviceType: EnumServiceType): List<EntityMachine>

    @Query("SELECT * FROM machines WHERE id = :id")
    abstract suspend fun get(id: UUID): EntityMachine?

    @Query("SELECT stack_order FROM machines WHERE machine_type = :machineType AND service_type = :serviceType ORDER BY stack_order DESC")
    abstract suspend fun getLastStackOrder(machineType: EnumMachineType, serviceType: EnumServiceType): Int?

    @Query("SELECT * FROM machines ORDER BY stack_order")
    abstract fun getAllAsLiveData(): LiveData<List<EntityMachine>>

    @Query("""
        SELECT m.*, 
            SUM(CASE WHEN mu.id IS NOT NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') THEN 1 ELSE 0 END) AS usage_for_the_day
        FROM machines m 
        LEFT JOIN machine_usages mu ON m.id = mu.machine_id
        WHERE machine_type = :machineType AND service_type = :serviceType
        GROUP BY m.id
        ORDER BY stack_order
    """)
    abstract fun getListAllAsLiveData(machineType: EnumMachineType?, serviceType: EnumServiceType?): LiveData<List<MachineListItem>>

    @Query("SELECT * FROM machines WHERE id = :id")
    abstract fun getMachineLiveData(id: UUID?): LiveData<EntityMachine?>

    @Query("""
        SELECT mu.id, machine_id, ma.machine_number, ma.created_at, mu.machine_id, mu.customer_id, mu.created_at AS activated, cu.name as customer_name, jos.job_order_id, jos.service_name, jos.svc_minutes, jos.svc_wash_type, jos.svc_machine_type, job_order_number, jos.price, jos.discounted_price, mu.sync
        FROM machine_usages mu
        LEFT JOIN machines ma ON mu.machine_id = ma.id
        LEFT JOIN customers cu ON mu.customer_id = cu.id
        LEFT JOIN job_order_services jos ON mu.job_order_service_id = jos.id
        LEFT JOIN job_orders jo ON jos.job_order_id = jo.id
        WHERE (mu.machine_id = :machineId OR :machineId IS NULL)
        AND customer_name LIKE '%' || :keyword || '%'
        AND (machine_type = :machineType OR :machineType IS NULL)
        AND (service_type = :serviceType OR :serviceType IS NULL)
        AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
        ORDER BY activated DESC
        LIMIT 20 OFFSET :offset
    """)
    abstract suspend fun getMachineUsage(machineId: UUID?, machineType: EnumMachineType?, serviceType: EnumServiceType?, keyword: String?, offset: Int, dateFrom: LocalDate?, dateTo: LocalDate?): List<EntityMachineUsageDetails>

    @Query("SELECT * FROM machines WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityMachine>

    @Query("SELECT * FROM machine_usages WHERE id = :machineUsageId")
    abstract suspend fun getMachineUsage(machineUsageId: UUID): EntityMachineUsageFull?

    @Query("SELECT * FROM machine_usages WHERE id = :id")
    abstract fun getMachineUsageAsLiveData(id: UUID?): LiveData<EntityMachineUsageFull?>

    @Upsert
    abstract suspend fun addRemarks(machineRemarks: EntityMachineRemarks)
}
