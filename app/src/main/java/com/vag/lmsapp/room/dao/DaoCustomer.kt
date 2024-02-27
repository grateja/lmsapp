package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.app.customers.list.CustomerListItem
import com.vag.lmsapp.app.customers.list.CustomerQueryResult
import com.vag.lmsapp.room.entities.EntityCustomer
import java.time.LocalDate
import java.util.UUID

@Dao
interface DaoCustomer : BaseDao<EntityCustomer> {
    @Query("SELECT * FROM customers WHERE id = :id AND deleted_at IS NULL")
    suspend fun get(id: UUID) : EntityCustomer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :keyword || '%' AND deleted_at IS NULL ORDER BY name LIMIT 10")
    suspend fun getAll(keyword: String) : List<EntityCustomer>

    @Query("SELECT crn FROM customers WHERE deleted_at IS NULL ORDER BY crn DESC")
    suspend fun getLastCRN() : String?

//    @Query("SELECT id, crn, name, address, (SELECT COUNT(*) FROM job_orders WHERE payment_id IS NULL AND customer_id = customers.id AND job_orders.deleted_at IS NULL and job_orders.void_date IS NULL) as unpaid FROM customers WHERE name LIKE '%' || :keyword || '%' OR crn like '%' || :keyword || '%' AND deleted_at IS NULL ORDER BY unpaid DESC, name ASC  LIMIT :itemPerPage OFFSET :offset")
    @Query("SELECT date(jo.created_at / 1000, 'unixepoch', 'localtime') as jo_date, date('now', 'localtime') as date_now, (c.id = :customerId) as selected, c.id, c.crn, c.name, c.address, COALESCE(COUNT(jo.id), 0) AS unpaid, MAX(jo.created_at) AS last_job_order, MAX(CASE WHEN jo.payment_id IS NULL AND date(jo.created_at / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') THEN jo.id ELSE null END) AS unpaid_jo_id_today " +
        " FROM customers c" +
        " LEFT JOIN job_orders jo ON jo.payment_id IS NULL AND jo.customer_id = c.id AND jo.deleted_at IS NULL AND jo.void_date IS NULL" +
        " WHERE (c.id = :customerId " +
        "    OR c.name LIKE '%' || :keyword || '%'" +
        "    OR c.crn LIKE '%' || :keyword || '%')" +
        "    AND c.deleted_at IS NULL" +
        " GROUP BY c.id, c.crn, c.name, c.address" +
        " ORDER BY selected DESC, unpaid DESC, c.name ASC LIMIT :itemPerPage OFFSET :offset")
    suspend fun getCustomersMinimal(keyword: String?, itemPerPage: Int, offset: Int, customerId: UUID?): List<CustomerMinimal>

    @Query("SELECT cu.*, " +
            "SUM(CASE WHEN jo.payment_id IS NOT NULL THEN 1 ELSE 0 END) AS paid_count, " +
            "COUNT(jo.id) as total_jo, MIN(jo.created_at) AS first_visit, MAX(jo.created_at) AS last_visit FROM customers cu LEFT JOIN job_orders jo ON jo.customer_id = cu.id WHERE " +
            "((:hideAllWithoutJO = 1 AND cu.id IN (SELECT DISTINCT customer_id FROM job_orders)) OR " +
            "(:hideAllWithoutJO = 0) AND (cu.name LIKE '%' || :keyword || '%' OR cu.crn LIKE '%' || :keyword || '%') AND cu.deleted_at IS NULL) " +
            "AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NULL AND datetime(cu.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND datetime(cu.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) " +
            "GROUP BY cu.id " +
            "ORDER BY " +
            "CASE WHEN :orderBy = 'Name' AND :sortDirection = 'ASC' THEN cu.name END ASC, " +
            "CASE WHEN :orderBy = 'Name' AND :sortDirection = 'DESC' THEN cu.name END DESC, " +
            "CASE WHEN :orderBy = 'First Visit' AND :sortDirection = 'ASC' THEN first_visit END ASC, " +
            "CASE WHEN :orderBy = 'First Visit' AND :sortDirection = 'DESC' THEN first_visit END DESC, " +
            "CASE WHEN :orderBy = 'Last Visit' AND :sortDirection = 'ASC' THEN last_visit END ASC, " +
            "CASE WHEN :orderBy = 'Last Visit' AND :sortDirection = 'DESC' THEN last_visit END DESC, " +
            "CASE WHEN :orderBy = 'Number of Job Orders' AND :sortDirection = 'ASC' THEN total_jo END ASC, " +
            "CASE WHEN :orderBy = 'Number of Job Orders' AND :sortDirection = 'DESC' THEN total_jo END DESC " +
            "LIMIT 20 OFFSET :offset")
    fun load(keyword: String?, orderBy: String?, sortDirection: String?, offset: Int, hideAllWithoutJO: Boolean, dateFrom: LocalDate?, dateTo: LocalDate?): List<CustomerListItem>


    @Query("SELECT COUNT(*) FROM customers WHERE (name LIKE '%' || :keyword || '%' OR crn like '%' || :keyword || '%' AND deleted_at IS NULL) " +
        "AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
                "(:dateFrom IS NOT NULL AND :dateTo IS NULL AND datetime(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
                "(:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND datetime(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) "
    )
    fun count(keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?) : Int

    @Transaction
    suspend fun getListItem(keyword: String?, orderBy: String?, sortDirection: String?, offset: Int, hideAllWithoutJO: Boolean, dateFrom: LocalDate?, dateTo: LocalDate?): CustomerQueryResult {
        return CustomerQueryResult(
            load(keyword, orderBy, sortDirection, offset, hideAllWithoutJO, dateFrom, dateTo),
            count(keyword, dateFrom, dateTo)
        )
    }

    @Query("SELECT EXISTS(SELECT * FROM customers WHERE name LIKE :name AND deleted_at IS NULL)")
    suspend fun checkName(name: String?): Boolean

    @Query("SELECT * FROM customers WHERE crn LIKE :crn AND deleted_at IS NULL LIMIT 1")
    suspend fun getCustomerMinimalByCRN(crn: String?): EntityCustomer?

    @Query("SELECT COUNT(*) FROM customers WHERE datetime(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom OR ( :dateTo IS NOT NULL AND datetime(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo )")
    fun getDashboardCustomer(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<Int>

    @Query("SELECT * FROM customers WHERE id = :customerId AND deleted_at IS NULL")
    fun getCustomerAsLiveData(customerId: UUID?): LiveData<EntityCustomer>

    @Query("SELECT COUNT(*) < :limit OR :limit = 0 FROM job_orders WHERE customer_id = :customerId AND payment_id IS NULL AND deleted_at IS NULL AND void_date IS NULL")
    fun canCreateJobOrder(customerId: UUID, limit: Int): LiveData<Boolean>
}