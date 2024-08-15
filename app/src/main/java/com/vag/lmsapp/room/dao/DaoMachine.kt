package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.app.machines.preview.machine_usages.MachineUsageAdvancedFilter
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.entities.EntityMachine
import com.vag.lmsapp.room.entities.EntityMachineRemarks
import com.vag.lmsapp.room.entities.EntityMachineUsageDetails
import com.vag.lmsapp.room.entities.EntityMachineUsageFull
import com.vag.lmsapp.util.EnumSortDirection
import com.vag.lmsapp.util.QueryResult
import com.vag.lmsapp.util.ResultCount
import java.time.LocalDate
import java.util.*

@Dao
interface DaoMachine : BaseDao<EntityMachine> {
    @Query("SELECT * FROM machines WHERE machine_type = :machineType AND service_type = :serviceType AND deleted = 0 ORDER BY stack_order")
    suspend fun getAll(machineType: EnumMachineType, serviceType: EnumServiceType): List<EntityMachine>

    @Query("SELECT * FROM machines WHERE id = :id")
    suspend fun get(id: UUID): EntityMachine?

    @Query("SELECT stack_order FROM machines WHERE machine_type = :machineType AND service_type = :serviceType AND deleted = 0 ORDER BY stack_order DESC LIMIT 1")
    suspend fun getLastStackOrder(machineType: EnumMachineType?, serviceType: EnumServiceType?): Int?

//    @Query("SELECT * FROM machines ORDER BY stack_order")
//    fun getAllAsLiveData(): LiveData<List<EntityMachine>>

    @Query("""
        SELECT m.*, 
            SUM(CASE WHEN mu.id IS NOT NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') THEN 1 ELSE 0 END) AS usage_for_the_day,
            COUNT(*) AS total_usage
        FROM machines m 
        LEFT JOIN machine_usages mu ON m.id = mu.machine_id
        WHERE machine_type = :machineType AND service_type = :serviceType AND m.deleted = 0
        GROUP BY m.id
        ORDER BY stack_order
    """)
    fun getListAllAsLiveData(machineType: EnumMachineType?, serviceType: EnumServiceType?): LiveData<List<MachineListItem>>

    @Query("SELECT * FROM machines WHERE id = :id")
    fun getMachineLiveData(id: UUID?): LiveData<EntityMachine?>

    @Query("""
        SELECT mu.id, machine_id, ma.machine_number, ma.created_at, mu.machine_id, mu.customer_id, mu.created_at AS activated, cu.name as customer_name, jos.job_order_id, jos.service_name, jos.svc_minutes, jos.svc_wash_type, jos.svc_machine_type, jos.svc_service_type, job_order_number, jos.price, jos.discounted_price, mu.sync
        FROM machine_usages mu
        LEFT JOIN machines ma ON mu.machine_id = ma.id
        LEFT JOIN customers cu ON mu.customer_id = cu.id
        LEFT JOIN job_order_services jos ON mu.job_order_service_id = jos.id
        LEFT JOIN job_orders jo ON jos.job_order_id = jo.id
        WHERE (mu.machine_id = :machineId OR :machineId IS NULL)
        AND (mu.customer_id = :customerId OR :customerId IS NULL)
        AND (customer_name LIKE '%' || :keyword || '%' OR job_order_number LIKE '%' || :keyword || '%')
        AND (machine_type = :machineType OR :machineType IS NULL)
        AND (service_type = :serviceType OR :serviceType IS NULL)
        AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
        ORDER BY
            CASE WHEN :orderBy = 'Customer' AND :sortDirection = 'ASC' THEN cu.name END ASC,
            CASE WHEN :orderBy = 'Date used' AND :sortDirection = 'ASC' THEN mu.created_at END ASC,
            CASE WHEN :orderBy = 'Customer' AND :sortDirection = 'DESC' THEN name END DESC,
            CASE WHEN :orderBy = 'Date used' AND :sortDirection = 'DESC' THEN mu.created_at END DESC
        LIMIT 20 OFFSET :offset
    """)
    suspend fun getMachineUsage(
        customerId: UUID?,
        machineId: UUID?,
        machineType: EnumMachineType?,
        serviceType: EnumServiceType?,
        keyword: String?,
        offset: Int,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        orderBy: String,
        sortDirection: EnumSortDirection
    ): List<EntityMachineUsageDetails>

    @Query("SELECT * FROM machines WHERE sync = 0 OR :forced")
    suspend fun unSynced(forced: Boolean): List<EntityMachine>

    @Query("SELECT * FROM machine_usages WHERE id = :machineUsageId")
    suspend fun getMachineUsage(machineUsageId: UUID): EntityMachineUsageFull?

    @Query("SELECT * FROM machine_usages WHERE id = :id")
    fun getMachineUsageAsLiveData(id: UUID?): LiveData<EntityMachineUsageFull?>

    @Upsert
    suspend fun addRemarks(machineRemarks: EntityMachineRemarks)

    @Query("UPDATE machines SET stack_order = :newOrder WHERE id = :machineId")
    suspend fun setStackOrder(machineId: UUID, newOrder: Int)

    @Transaction
    suspend fun rearrange(machines: List<MachineListItem>) {
        machines.forEach {
            setStackOrder(it.machine.id, it.machine.stackOrder!!)
        }
    }

    @Query("""SELECT (
        SELECT COUNT(*)
        FROM machine_usages mu
        LEFT JOIN machines ma ON mu.machine_id = ma.id
        LEFT JOIN customers cu ON mu.customer_id = cu.id
        LEFT JOIN job_order_services jos ON mu.job_order_service_id = jos.id
        LEFT JOIN job_orders jo ON jos.job_order_id = jo.id
        WHERE (mu.machine_id = :machineId OR :machineId IS NULL)
        AND (mu.customer_id = :customerId OR :customerId IS NULL)
        AND (cu.name LIKE '%' || :keyword || '%' OR job_order_number LIKE '%' || :keyword || '%')
        AND (machine_type = :machineType OR :machineType IS NULL)
        AND (service_type = :serviceType OR :serviceType IS NULL)
        AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR
            (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(mu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
    ) AS filtered, (
        SELECT COUNT(*)
        FROM machine_usages mu
        LEFT JOIN machines ma ON mu.machine_id = ma.id
        LEFT JOIN customers cu ON mu.customer_id = cu.id
        WHERE (mu.machine_id = :machineId OR :machineId IS NULL)
        AND (mu.customer_id = :customerId OR :customerId IS NULL)
        AND (machine_type = :machineType OR :machineType IS NULL)
        AND (service_type = :serviceType OR :serviceType IS NULL)
    ) AS total
    """)
    suspend fun resultCount(customerId: UUID?, machineId: UUID?, machineType: EnumMachineType?, serviceType: EnumServiceType?, keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?): ResultCount

    suspend fun filterMachineUsage(keyword: String?, offset: Int, filter: MachineUsageAdvancedFilter): QueryResult<EntityMachineUsageDetails> {
        println("date filter")
        println(filter.dateFilter)
        return QueryResult(
            getMachineUsage(
                filter.customerId,
                filter.machineId,
                filter.machineType,
                filter.serviceType,
                keyword,
                offset,
                filter.dateFilter?.dateFrom,
                filter.dateFilter?.dateTo,
                filter.orderBy,
                filter.sortDirection
            ),
            resultCount(filter.customerId, filter.machineId, filter.machineType, filter.serviceType, keyword, filter.dateFilter?.dateFrom, filter.dateFilter?.dateTo)
        )
    }
}
