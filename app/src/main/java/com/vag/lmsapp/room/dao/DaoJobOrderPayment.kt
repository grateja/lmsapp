package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.app.payment_list.PaymentQueryResult
import com.vag.lmsapp.room.entities.*
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Dao
interface DaoJobOrderPayment {
    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    fun getPaymentWithJobOrdersAsLiveData(id: UUID) : LiveData<EntityJobOrderPaymentFull?>

    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    suspend fun getPaymentWithJobOrders(id: UUID) : EntityJobOrderPaymentFull?

    @Query("SELECT * FROM job_order_payments WHERE id = :id")
    suspend fun get(id: UUID?): EntityJobOrderPayment?

    @Query("UPDATE job_orders SET payment_id = :paymentId WHERE id IN (:jobOrderIds)")
    fun setJobOrderPayment(paymentId: UUID, jobOrderIds: List<UUID>)

    @Upsert
    fun insertPayment(payment: EntityJobOrderPayment)

    @Transaction
    suspend fun save(
        entityJobOrderPayment: EntityJobOrderPayment,
        jobOrderIds: List<UUID>
    ) : EntityJobOrderPayment {
        insertPayment(entityJobOrderPayment)
        setJobOrderPayment(entityJobOrderPayment.id, jobOrderIds)
        return entityJobOrderPayment
    }

    @Query("UPDATE job_order_payments SET deleted = 1 WHERE id = :paymentId")
    fun unlinkPayment(paymentId: UUID/*, voidBy: UUID, remarks: String, deletedAt: Instant = Instant.now()*/)

    @Query("UPDATE job_orders SET payment_id = null WHERE payment_id = :paymentId")
    fun unlinkJobOrders(paymentId: UUID)

    @Transaction
    suspend fun deletePayment(paymentId: UUID, voidBy: UUID, remarks: String) {
        unlinkJobOrders(paymentId)
        unlinkPayment(paymentId/*, voidBy, remarks*/)
    }

    @Query("SELECT DISTINCT cashless_provider FROM job_order_payments WHERE cashless_provider IS NOT NULL ORDER BY cashless_provider")
    fun getCashlessProviders(): LiveData<List<String>>

    @Query("SELECT jop.* FROM job_order_payments jop JOIN job_orders jo ON jop.id = jo.payment_id WHERE jo.id = :jobOrderId AND jo.deleted = 0")
    suspend fun getByJobOrderId(jobOrderId: UUID?) : EntityJobOrderPaymentFull?

    @Query("SELECT SUM(amount_due) FROM job_order_payments WHERE payment_method = 1 AND deleted = 0 AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom OR ( :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo )")
    fun getCashCollections(dateFrom: LocalDate, dateTo: LocalDate?) : LiveData<Float>

    @Query("SELECT cashless_provider, COUNT(*) as count, SUM(cashless_amount) as amount " +
            "FROM job_order_payments " +
            "WHERE payment_method = 2 " +
            "    AND deleted = 0" +
//            "    AND void_date IS NULL " +
            "    AND (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom " +
            "    OR (:dateTo IS NOT NULL " +
            "    AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) " +
            "GROUP BY cashless_provider " +
            "UNION " +
            "SELECT 'Total', COUNT(*), SUM(cashless_amount) " +
            "FROM job_order_payments " +
            "WHERE payment_method = 2 " +
            "    AND deleted = 0" +
//            "    AND void_date IS NULL " +
            "    AND (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom " +
            "    OR (:dateTo IS NOT NULL " +
            "    AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))")
    fun getCashlessPayments(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<List<EntityCashlessPaymentAggrResult>>

    @Query("SELECT * FROM job_orders WHERE payment_id = :paymentId AND deleted = 0 ORDER BY created_at DESC")
    fun getJobOrdersByPaymentId(paymentId: UUID): LiveData<List<JobOrderPaymentMinimal>>

    @Query("SELECT " +
            "    p.id, payment_method, amount_due, or_number, cashless_provider, c.name, p.created_at, " +
            "    GROUP_CONCAT(jo.job_order_number, ', ') AS job_order_reference " +
            "FROM job_order_payments AS p " +
            "LEFT JOIN job_orders AS jo ON p.id = jo.payment_id " +
            "LEFT JOIN customers AS c ON jo.customer_id = c.id " +
            "WHERE (or_number LIKE '%' || :keyword || '%' OR c.name LIKE '%' || :keyword || '%' OR jo.job_order_number LIKE '%' || :keyword || '%')" +
            " AND p.deleted = 0 "+
            "AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) " +
            "GROUP BY p.id " +
            "ORDER BY p.created_at DESC " +
            " LIMIT 20 OFFSET :offset ")
    fun load(keyword: String?, offset: Int, dateFrom: LocalDate?, dateTo: LocalDate?): List<EntityJobOrderPaymentListItem>

    @Query("SELECT COUNT(DISTINCT p.id) as total_count, SUM(jo.discounted_amount) as total_sum " +
            "    ,GROUP_CONCAT(jo.job_order_number, ', ') AS job_order_reference " +
            "FROM job_order_payments AS p " +
            "LEFT JOIN job_orders AS jo ON p.id = jo.payment_id " +
            "LEFT JOIN customers AS c ON jo.customer_id = c.id " +
            "WHERE (or_number LIKE '%' || :keyword || '%' OR c.name LIKE '%' || :keyword || '%')" +
            "AND p.deleted = 0 " +
            "AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
            "(:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(p.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) ")
    fun count(keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?): QueryAggrResult?

    @Transaction
    suspend fun queryResult(keyword: String?, offset: Int, dateFilter: DateFilter?): PaymentQueryResult {
        return PaymentQueryResult(
            load(keyword, offset, dateFilter?.dateFrom, dateFilter?.dateTo),
            count(keyword, dateFilter?.dateFrom, dateFilter?.dateTo)
        )
    }
}