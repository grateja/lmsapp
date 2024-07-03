package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.export_options.data.ExportDataDeliveryCharge
import com.vag.lmsapp.app.export_options.data.ExportDataExpenses
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrder
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderExtras
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderProduct
import com.vag.lmsapp.app.export_options.data.ExportDataJobOrderService
import com.vag.lmsapp.app.export_options.data.ExportDataMachineUsage
import com.vag.lmsapp.app.export_options.data.ExportDataNewCustomers
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import java.time.LocalDate

@Dao
interface DaoExport {

    @Query("""
        SELECT jo.created_at, jo.job_order_number, cu.name as customer_name, jo.subtotal,
            COALESCE(jo.discount_in_peso, 0) AS discount_in_peso, (di.name || '(-'||di.value||'%)') AS discount_str,
            jo.discounted_amount, jop.created_at AS date_paid, 
            (CASE WHEN jop.payment_method = 1 THEN 'Cash' ELSE jop.cashless_provider END) as payment_method,
            u.name AS received_by
        FROM job_orders jo
            LEFT JOIN customers cu ON cu.id = jo.customer_id
            LEFT JOIN job_order_discounts di ON di.id = jo.id
            LEFT JOIN job_order_payments jop ON jop.id = jo.payment_id
            LEFT JOIN users u ON u.id = jop.user_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND void_by IS NULL AND jo.deleted = 0
    """)
    suspend fun jobOrders(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataJobOrder>

    @Query("""
        SELECT jo.created_at, jo.job_order_number, cu.name as customer_name, jo.subtotal,
            COALESCE(jo.discount_in_peso, 0) AS discount_in_peso, (di.name || '(-'||di.value||'%)') AS discount_str,
            jo.discounted_amount, jop.created_at AS date_paid, 
            (CASE WHEN jop.payment_method = 1 THEN 'Cash' ELSE jop.cashless_provider END) as payment_method,
            u.name AS received_by
        FROM job_orders jo
            LEFT JOIN customers cu ON cu.id = jo.customer_id
            LEFT JOIN job_order_discounts di ON di.id = jo.id
            LEFT JOIN job_order_payments jop ON jop.id = jo.payment_id
            LEFT JOIN users u ON u.id = jop.user_id
        WHERE void_by IS NULL AND jo.deleted = 0 AND payment_id IS NULL
    """)
    suspend fun unpaidJobOrders(): List<ExportDataJobOrder>

    @Query("""
        SELECT COUNT(*)
        FROM job_orders
        WHERE DATE(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND void_by IS NULL AND deleted = 0
    """)
    fun jobOrdersCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT jo.created_at, jo.job_order_number, cu.name AS customer_name,
            jos.service_name, jos.svc_machine_type,
            jos.quantity, jos.discounted_price
        FROM job_order_services jos
        JOIN job_orders jo ON jo.id = jos.job_order_id
        JOIN customers cu ON cu.id = jo.customer_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jos.void = 0 AND jos.deleted = 0
    """)
    suspend fun jobOrderServices(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataJobOrderService>

    @Query("""
        SELECT COUNT(*)
        FROM job_order_services jos
        JOIN job_orders jo ON jo.id = jos.job_order_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jos.void = 0 AND jos.deleted = 0
    """)
    fun jobOrderServicesCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT jo.created_at, jo.job_order_number, cu.name AS customer_name,
            jop.product_name, jop.product_type, jop.measure_unit,
            jop.quantity, jop.discounted_price
        FROM job_order_products jop
        JOIN job_orders jo ON jo.id = jop.job_order_id
        JOIN customers cu ON cu.id = jo.customer_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jop.void = 0 AND jop.deleted = 0
    """)
    suspend fun jobOrderProducts(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataJobOrderProduct>

    @Query("""
        SELECT COUNT(*)
        FROM job_order_products jop
        JOIN job_orders jo ON jo.id = jop.job_order_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jop.void = 0 AND jop.deleted = 0
    """)
    fun jobOrderProductsCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT jo.created_at, jo.job_order_number, cu.name AS customer_name,
            joe.extras_name, joe.category,
            joe.quantity, joe.discounted_price
        FROM job_order_extras joe
        JOIN job_orders jo ON jo.id = joe.job_order_id
        JOIN customers cu ON cu.id = jo.customer_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND joe.void = 0 AND joe.deleted = 0
    """)
    suspend fun jobOrderExtras(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataJobOrderExtras>

    @Query("""
        SELECT COUNT(*)
        FROM job_order_extras joe
        JOIN job_orders jo ON jo.id = joe.job_order_id
        WHERE DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND joe.void = 0 AND joe.deleted = 0
    """)
    fun jobOrderExtrasCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT mu.created_at AS activated,
            cu.name AS customer_name,
            m.machine_number, m.service_type, m.machine_type,
            jos.svc_minutes, jos.service_name,
            jo.job_order_number, jo.created_at
        FROM machine_usages mu
            JOIN machines m ON m.id = mu.machine_id
            JOIN job_order_services jos ON jos.id = mu.job_order_service_id
            JOIN job_orders jo ON jo.id = jos.job_order_id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE DATE(mu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jos.void = 0 AND jos.deleted = 0
            AND m.deleted = 0
            AND mu.deleted = 0
    """)
    suspend fun machineUsages(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataMachineUsage>

    @Query("""
        SELECT COUNT(*)
        FROM machine_usages mu
            JOIN machines m ON m.id = mu.machine_id
            JOIN job_order_services jos ON jos.id = mu.job_order_service_id
            JOIN job_orders jo ON jo.id = jos.job_order_id
        WHERE DATE(mu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jos.void = 0 AND jos.deleted = 0
            AND m.deleted = 0
            AND mu.deleted = 0
    """)
    fun machineUsagesCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT jodc.vehicle, jodc.delivery_option, jodc.discounted_price, jodc.distance,
            jo.job_order_number, jo.created_at,
            cu.name AS customer_name
        FROM job_order_delivery_charges jodc
            JOIN job_orders jo ON jo.id = jodc.id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE DATE(jodc.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jodc.void = 0 AND jodc.deleted = 0
    """)
    suspend fun deliveryCharges(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataDeliveryCharge>

    @Query("""
        SELECT COUNT(*)
        FROM job_order_delivery_charges jodc
            JOIN job_orders jo ON jo.id = jodc.id
        WHERE DATE(jodc.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND jo.void_by IS NULL AND jo.deleted = 0
            AND jodc.void = 0 AND jodc.deleted = 0
    """)
    fun deliveryChargesCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT ex.created_at, ex.remarks, ex.amount, ex.tag,
            u.name AS created_by
        FROM expenses ex
            JOIN users u ON u.id = ex.created_by
        WHERE DATE(ex.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND ex.deleted = 0
    """)
    suspend fun expenses(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataExpenses>

    @Query("""
        SELECT COUNT(*)
        FROM expenses
        WHERE DATE(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND deleted = 0
    """)
    fun expensesCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT
            cu.name, cu.crn, cu.contact_number, cu.address, cu.remarks,
            MAX(jo.created_at) AS last_jo,
            MIN(jo.created_at) AS first_jo,
            SUM(CASE WHEN jos.svc_machine_type = 1 OR jos.svc_machine_type = 3 THEN jos.quantity ELSE 0 END) as total_washes,
            SUM(CASE WHEN jos.svc_machine_type = 2 OR jos.svc_machine_type = 4 THEN jos.quantity ELSE 0 END) as total_dries,
            COUNT(DISTINCT jo.id) as total_job_orders
        FROM customers cu
            LEFT JOIN job_orders jo ON jo.customer_id = cu.id 
                AND jo.void_by IS NULL 
                AND jo.deleted = 0
            LEFT JOIN job_order_services jos ON jos.job_order_id = jo.id 
                AND jos.deleted = 0 
                AND jos.void = 0
        WHERE DATE(cu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND cu.deleted = 0
        GROUP BY cu.id
    """)
    suspend fun customers(dateFrom: LocalDate, dateTo: LocalDate): List<ExportDataNewCustomers>

    @Query("""
        SELECT COUNT(*) FROM customers
        WHERE DATE(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo
            AND deleted = 0
    """)
    fun customersCount(dateFrom: LocalDate, dateTo: LocalDate): LiveData<Int>

    @Query("""
        SELECT COUNT(*)
        FROM job_orders
        WHERE void_by IS NULL AND deleted = 0 AND payment_id IS NULL
    """)
    fun unpaidJobOrdersCount(): LiveData<Int>
}