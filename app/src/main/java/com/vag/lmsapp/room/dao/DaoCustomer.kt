package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.customers.list.CustomerListItem
import com.vag.lmsapp.app.customers.list.CustomerQueryResult
import com.vag.lmsapp.app.customers.list.advanced_filter.CustomersAdvancedFilter
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilter
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.util.ResultCount
import java.time.LocalDate
import java.util.UUID

@Dao
interface DaoCustomer : BaseDao<EntityCustomer> {
    @Query("""
        SELECT * FROM customers WHERE id = (
            SELECT customer_id FROM job_orders WHERE payment_id = :paymentId
        )
    """)
    fun getCustomerByPaymentIdAsLiveData(paymentId: UUID?): EntityCustomer?

    @Query("""
        SELECT * 
        FROM customers 
        WHERE id = :id AND deleted = 0
    """)
    suspend fun get(id: UUID): EntityCustomer?

    @Query("""
        SELECT * 
        FROM customers 
        WHERE name LIKE '%' || :keyword || '%' AND deleted = 0 
        ORDER BY name 
        LIMIT 10
    """)
    suspend fun getAll(keyword: String): List<EntityCustomer>

    @Query("""
        SELECT crn 
        FROM customers 
        WHERE deleted = 0 
        ORDER BY crn DESC
    """)
    suspend fun getLastCRN(): String?

    @Query("""
        SELECT 
            date(jo.created_at / 1000, 'unixepoch', 'localtime') as jo_date,
            date('now', 'localtime') as date_now,
            (c.id = :customerId) as selected,
            c.id,
            c.crn,
            c.name,
            c.address,
            COALESCE(COUNT(jo.id), 0) AS unpaid,
            MAX(jo.created_at) AS last_job_order
        FROM 
            customers c
        LEFT JOIN 
            job_orders jo ON jo.payment_id IS NULL 
                          AND jo.customer_id = c.id 
                          AND jo.deleted = 0 
                          AND jo.void_date IS NULL
        WHERE 
            (c.id = :customerId 
             OR c.name LIKE '%' || :keyword || '%'
             OR c.crn LIKE '%' || :keyword || '%')
            AND c.deleted = 0
        GROUP BY 
            c.id, c.crn, c.name, c.address
        ORDER BY 
            selected DESC, unpaid DESC, c.name ASC 
        LIMIT :itemPerPage OFFSET :offset
    """)
    suspend fun getCustomersMinimal(keyword: String?, itemPerPage: Int, offset: Int, customerId: UUID?): List<CustomerMinimal>

    @Query("""
        SELECT cu.*, 
               SUM(CASE WHEN jo.id IS NOT NULL AND jo.payment_id IS NOT NULL THEN 1 ELSE 0 END) AS paid_count, 
               SUM(CASE WHEN jo.id IS NOT NULL AND jo.void_date IS NULL THEN 1 ELSE 0 END) as total_jo, 
               MIN(CASE WHEN jo.id IS NOT NULL AND jo.void_date IS NULL THEN jo.created_at END) AS first_visit, 
               MAX(CASE WHEN jo.id IS NOT NULL AND jo.void_date IS NULL THEN jo.created_at END) AS last_visit 
        FROM 
            customers cu 
        LEFT JOIN 
            job_orders jo ON jo.customer_id = cu.id
        WHERE 
            ((:hideAllWithoutJO = 1 AND cu.id IN (SELECT DISTINCT customer_id FROM job_orders WHERE void_date IS NULL) OR :hideAllWithoutJO = 0)
            AND (cu.name LIKE '%' || :keyword || '%' OR cu.crn LIKE '%' || :keyword || '%') AND cu.deleted = 0)
            AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
            (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(cu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
            (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(cu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
        GROUP BY 
            cu.id 
        ORDER BY 
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'ASC' THEN cu.name END ASC, 
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'DESC' THEN cu.name END DESC, 
            CASE WHEN :orderBy = 'First Visit' AND :sortDirection = 'ASC' THEN first_visit END ASC, 
            CASE WHEN :orderBy = 'First Visit' AND :sortDirection = 'DESC' THEN first_visit END DESC, 
            CASE WHEN :orderBy = 'Last Visit' AND :sortDirection = 'ASC' THEN last_visit END ASC, 
            CASE WHEN :orderBy = 'Last Visit' AND :sortDirection = 'DESC' THEN last_visit END DESC, 
            CASE WHEN :orderBy = 'Number of Job Orders' AND :sortDirection = 'ASC' THEN total_jo END ASC, 
            CASE WHEN :orderBy = 'Number of Job Orders' AND :sortDirection = 'DESC' THEN total_jo END DESC 
        LIMIT 20 OFFSET :offset
    """)
    fun load(keyword: String?, orderBy: String?, sortDirection: String?, offset: Int, hideAllWithoutJO: Boolean, dateFrom: LocalDate?, dateTo: LocalDate?): List<CustomerListItem>

    @Query("""
        SELECT
        (
            SELECT COUNT(DISTINCT cu.id) 
            FROM customers cu
        LEFT JOIN 
            job_orders jo ON jo.customer_id = cu.id
        WHERE
            (:hideAllWithoutJO = 1 AND cu.id IN (SELECT DISTINCT customer_id FROM job_orders WHERE void_date IS NULL) OR 
            (:hideAllWithoutJO = 0)) AND
                (name LIKE '%' || :keyword || '%' OR crn like '%' || :keyword || '%' AND cu.deleted = 0) 
                AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(cu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(cu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
        ) AS filtered,
        (
            SELECT COUNT(*) 
            FROM customers 
            WHERE deleted = 0
        ) AS total
    """)
    fun count(keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?, hideAllWithoutJO: Boolean): ResultCount

    @Transaction
    suspend fun getListItem(keyword: String?, offset: Int, advancedFilter: CustomersAdvancedFilter): CustomerQueryResult {
        return CustomerQueryResult(
            load(keyword, advancedFilter.orderBy, advancedFilter.sortDirection.toString(), offset, advancedFilter.hideAllWithoutJo, advancedFilter.dateFilter?.dateFrom, advancedFilter.dateFilter?.dateTo),
            count(keyword, advancedFilter.dateFilter?.dateFrom, advancedFilter.dateFilter?.dateTo, advancedFilter.hideAllWithoutJo)
        )
    }

    @Query("""
        SELECT EXISTS(
            SELECT * 
            FROM customers 
            WHERE name LIKE :name AND deleted = 0
        )
    """)
    suspend fun checkName(name: String?): Boolean

    @Query("""
        SELECT * 
        FROM customers 
        WHERE crn LIKE :crn AND deleted = 0 
        LIMIT 1
    """)
    suspend fun getCustomerMinimalByCRN(crn: String?): EntityCustomer?

    @Query("""
        SELECT COUNT(*) < :limit OR :limit = 0 
        FROM job_orders 
        WHERE customer_id = :customerId 
              AND payment_id IS NULL 
              AND deleted = 0 
              AND void_date IS NULL
    """)
    fun canCreateJobOrder(customerId: UUID, limit: Int): LiveData<Boolean>

    @Query("""
        SELECT * 
        FROM customers 
        WHERE id = :customerId AND deleted = 0
    """)
    fun getCustomerAsLiveData(customerId: UUID?): LiveData<EntityCustomer>

    @Query("""
        SELECT COUNT(*) 
        FROM customers 
        WHERE (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
               OR (:dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
              AND deleted = 0
    """)
    fun getDashboardCustomer(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<Int>
}
