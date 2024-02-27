package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.entities.EntityAvailableService
import com.vag.lmsapp.room.entities.EntityCustomerQueueService
import com.vag.lmsapp.room.entities.EntityJobOrderService
import java.util.*

@Dao
interface DaoJobOrderQueues {
//    @Query("SELECT job_order_id, job_order_number, customer_id, jos.svc_machine_type as machine_type, customer_name FROM job_orders jo JOIN job_order_services jos ON job_order_id = jo.id LEFT JOIN services w ON w.id = service_id WHERE jos.svc_machine_type = :machineType AND jos.quantity > used GROUP BY customer_name, job_order_number, job_order_id")
//    @Query("SELECT job_order_id, job_order_number, customer_id, jos.svc_machine_type as machine_type, cu.name as customer_name FROM job_orders jo JOIN customers cu ON jo.customer_id = cu.id JOIN job_order_services jos ON job_order_id = jo.id LEFT JOIN services w ON w.id = service_id WHERE jos.svc_machine_type = :machineType AND jos.quantity > used GROUP BY customer_name")
    @Query("SELECT SUM(quantity - used) as available, customer_id, address, crn, jos.svc_machine_type as machine_type, cu.name as customer_name, MAX(jo.created_at) AS latest_job_order" +
            " FROM job_orders jo" +
            " JOIN customers cu ON jo.customer_id = cu.id" +
            " JOIN job_order_services jos ON job_order_id = jo.id" +
            " LEFT JOIN services w ON w.id = service_id" +
            " WHERE jos.svc_machine_type = :machineType" +
            "    AND jos.quantity > used" +
            "    AND jos.deleted_at IS NULL" +
            " GROUP BY customer_name" +
            " ORDER BY latest_job_order ASC")
    fun getCustomersByMachineType(machineType: EnumMachineType?) : LiveData<List<EntityCustomerQueueService>?>

    @Query("SELECT jo.id, SUM(jo.quantity - used) as available, service_name, svc_minutes as minutes, service_id, job_order_id, svc_wash_type, svc_machine_type" +
            " FROM job_order_services jo" +
            " JOIN job_orders on job_orders.id = job_order_id" +
            " WHERE customer_id = :customerId" +
            "    AND jo.svc_machine_type = :machineType" +
            "    AND (quantity - used) > 0" +
            " GROUP BY service_name")
    suspend fun getAvailableWashes(customerId: UUID, machineType: EnumMachineType) : List<EntityAvailableService>

    @Query("SELECT * FROM job_order_services WHERE id = :id")
    suspend fun get(id: UUID?): EntityJobOrderService?

    @Query("SELECT * FROM job_order_services WHERE id = :id")
    fun getAsLiveData(id: UUID?): LiveData<EntityJobOrderService?>

    @Query("SELECT jo.id, SUM(jo.quantity - used) as available, service_name, svc_minutes as minutes, service_id, job_order_id, svc_wash_type, svc_machine_type" +
            " FROM job_order_services jo" +
            "    JOIN job_orders on job_orders.id = job_order_id" +
            " WHERE customer_id = :customerId" +
            "    AND jo.svc_machine_type = :machineType" +
            "    AND (quantity - used) > 0 " +
            " GROUP BY service_name")
    fun getAvailableServicesAsLiveData(customerId: UUID, machineType: EnumMachineType): LiveData<List<EntityAvailableService>?>
}