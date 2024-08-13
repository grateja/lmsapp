package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.app.payment_list.PaymentQueryResult
import com.vag.lmsapp.app.payment_list.advanced_filter.JobOrderPaymentAdvancedFilter
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.util.EnumSortDirection
import com.vag.lmsapp.util.ResultCount
import java.time.LocalDate
import java.util.*

@Dao
interface DaoJobOrderPayment {

    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    fun getPaymentWithJobOrdersAsLiveData(id: UUID): LiveData<EntityJobOrderPaymentFull?>

    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    suspend fun getPaymentWithJobOrders(id: UUID): EntityJobOrderPaymentFull?

    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    suspend fun get(id: UUID?): EntityJobOrderPayment?

    @Query("""
        UPDATE job_orders SET payment_id = :paymentId WHERE id IN (:jobOrderIds)
    """)
    fun setJobOrderPayment(paymentId: UUID, jobOrderIds: List<UUID>)

    @Upsert
    fun insertPayment(payment: EntityJobOrderPayment)

    @Transaction
    suspend fun save(
        entityJobOrderPayment: EntityJobOrderPayment,
        jobOrderIds: List<UUID>
    ): EntityJobOrderPayment {
        insertPayment(entityJobOrderPayment)
        setJobOrderPayment(entityJobOrderPayment.id, jobOrderIds)
        return entityJobOrderPayment
    }

    @Query("""
        UPDATE job_order_payments SET deleted = 1 WHERE id = :paymentId
    """)
    fun unlinkPayment(paymentId: UUID)

    @Query("""
        UPDATE job_orders SET payment_id = null WHERE payment_id = :paymentId
    """)
    fun unlinkJobOrders(paymentId: UUID)

    @Transaction
    suspend fun deletePayment(paymentId: UUID, voidBy: UUID, remarks: String) {
        unlinkJobOrders(paymentId)
        unlinkPayment(paymentId)
    }

    @Query("""
        SELECT DISTINCT cashless_provider FROM job_order_payments WHERE cashless_provider IS NOT NULL ORDER BY cashless_provider
    """)
    fun getCashlessProviders(): LiveData<List<String>>

    @Query("""
        SELECT jop.* FROM job_order_payments jop 
        JOIN job_orders jo ON jop.id = jo.payment_id 
        WHERE jo.id = :jobOrderId AND jo.deleted = 0
    """)
    suspend fun getByJobOrderId(jobOrderId: UUID?): EntityJobOrderPaymentFull?

    @Query("""
        SELECT SUM(amount_due) FROM job_order_payments 
        WHERE payment_method = 1 AND deleted = 0 
        AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
        OR (:dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)
    """)
    fun getCashCollections(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<Float>

    @Query("""
        SELECT cashless_provider, COUNT(*) as count, SUM(cashless_amount) as amount 
        FROM job_order_payments 
        WHERE payment_method = 2 
        AND deleted = 0
        AND (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
        OR (:dateTo IS NOT NULL 
        AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
        GROUP BY cashless_provider 
        UNION 
        SELECT 'Total', COUNT(*), SUM(cashless_amount) 
        FROM job_order_payments 
        WHERE payment_method = 2 
        AND deleted = 0
        AND (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
        OR (:dateTo IS NOT NULL 
        AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
    """)
    fun getCashlessPayments(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<List<EntityCashlessPaymentAggrResult>>

    @Query("""
        SELECT 
            p.id, payment_method, amount_due, or_number, cashless_provider, c.id AS customer_id, c.name, p.created_at, 
            GROUP_CONCAT(jo.job_order_number, ', ') AS job_order_reference,
            p.sync 
        FROM job_order_payments AS p 
        LEFT JOIN job_orders AS jo ON p.id = jo.payment_id 
        LEFT JOIN customers AS c ON jo.customer_id = c.id 
        WHERE (or_number LIKE '%' || :keyword || '%' OR c.name LIKE '%' || :keyword || '%' OR jo.job_order_number LIKE '%' || :keyword || '%') 
        AND p.deleted = 0
        AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
        (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
        (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
        GROUP BY p.id 
        ORDER BY
            CASE WHEN :orderBy = 'Amount' AND :sortDirection = 'ASC' THEN p.amount_due END ASC,
            CASE WHEN :orderBy = 'Date Paid' AND :sortDirection = 'ASC' THEN p.created_at END ASC,
            CASE WHEN :orderBy = 'Customer Name' AND :sortDirection = 'ASC' THEN c.name END ASC,
            CASE WHEN :orderBy = 'Amount' AND :sortDirection = 'DESC' THEN jo.discounted_amount END DESC,
            CASE WHEN :orderBy = 'Date Paid' AND :sortDirection = 'DESC' THEN p.created_at END DESC,
            CASE WHEN :orderBy = 'Customer Name' AND :sortDirection = 'DESC' THEN c.name END DESC
        LIMIT 20 OFFSET :offset
    """)
    fun load(keyword: String?, offset: Int, dateFrom: LocalDate?, dateTo: LocalDate?, orderBy: String?, sortDirection: EnumSortDirection?): List<EntityJobOrderPaymentListItem>

    @Query("""
        SELECT(
            SELECT COUNT(DISTINCT p.id)
                FROM job_order_payments AS p 
                    LEFT JOIN job_orders AS jo ON p.id = jo.payment_id 
                    LEFT JOIN customers AS c ON jo.customer_id = c.id 
                WHERE (or_number LIKE '%' || :keyword || '%' OR c.name LIKE '%' || :keyword || '%' OR jo.job_order_number LIKE '%' || :keyword || '%') 
                    AND p.deleted = 0 
                    AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                    (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                    (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)
                )
            ) AS filtered,
            (
            SELECT COUNT(DISTINCT id)
                FROM job_order_payments 
                WHERE deleted = 0
            ) AS total
    """)
    fun count(keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?): ResultCount?

    @Transaction
    suspend fun queryResult(keyword: String?, offset: Int, advancedFilter: JobOrderPaymentAdvancedFilter?): PaymentQueryResult {
        return PaymentQueryResult(
            load(keyword, offset, advancedFilter?.dateFilter?.dateFrom, advancedFilter?.dateFilter?.dateTo, advancedFilter?.orderBy, advancedFilter?.sortDirection),
            count(keyword, advancedFilter?.dateFilter?.dateFrom, advancedFilter?.dateFilter?.dateTo)
        )
    }

    @Query("""
        SELECT * FROM job_orders WHERE payment_id = :paymentId AND deleted = 0 ORDER BY created_at DESC
    """)
    fun getJobOrdersByPaymentId(paymentId: UUID): LiveData<List<JobOrderPaymentMinimal>>
}
