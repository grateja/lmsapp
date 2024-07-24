package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.vag.lmsapp.app.expenses.ExpenseItemFull
import com.vag.lmsapp.app.expenses.ExpenseQueryResult
import com.vag.lmsapp.app.expenses.advanced_filter.ExpensesAdvancedFilter
import com.vag.lmsapp.room.entities.EntityExpensesAggrResult
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityExpenseFull
import com.vag.lmsapp.util.EnumSortDirection
import com.vag.lmsapp.util.ResultCount
import java.time.LocalDate
import java.util.UUID

@Dao
interface DaoExpense : BaseDao<EntityExpense> {

    @Query("""
        SELECT * 
        FROM expenses 
        WHERE id = :id AND deleted = 0
    """)
    abstract suspend fun get(id: UUID): EntityExpense?

    @Query("""
        SELECT * 
        FROM expenses 
        WHERE remarks LIKE '%' || :keyword || '%' AND deleted = 0 
              AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                   (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                   (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
        ORDER BY
            CASE WHEN :orderBy = 'Amount' AND :sortDirection = 'ASC' THEN amount END ASC,
            CASE WHEN :orderBy = 'Date Created' AND :sortDirection = 'ASC' THEN created_at END ASC,
            CASE WHEN :orderBy = 'Remarks' AND :sortDirection = 'ASC' THEN remarks END ASC,
            CASE WHEN :orderBy = 'Amount' AND :sortDirection = 'DESC' THEN amount END DESC,
            CASE WHEN :orderBy = 'Date Created' AND :sortDirection = 'DESC' THEN created_at END DESC,
            CASE WHEN :orderBy = 'Remarks' AND :sortDirection = 'DESC' THEN remarks END DESC
        LIMIT 20 OFFSET :offset
    """)
    suspend fun load(keyword: String?, offset: Int, dateFrom: LocalDate?, dateTo: LocalDate?, orderBy: String?, sortDirection: EnumSortDirection?): List<ExpenseItemFull>

    @Transaction
    suspend fun queryResult(keyword: String?, offset: Int, advancedFilter: ExpensesAdvancedFilter): ExpenseQueryResult {
        return ExpenseQueryResult(
            load(keyword, offset, advancedFilter.dateFilter?.dateFrom, advancedFilter.dateFilter?.dateTo, advancedFilter.orderBy, advancedFilter.sortDirection),
            count(keyword, advancedFilter.dateFilter?.dateFrom, advancedFilter.dateFilter?.dateTo)
        )
    }

    @Query("""SELECT(
        SELECT COUNT(*)
            FROM expenses 
            WHERE remarks LIKE '%' || :keyword || '%' AND deleted = 0 
                  AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                   (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                   (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) 
        ) AS filtered, (
            SELECT COUNT(*)
                FROM expenses 
                WHERE deleted = 0 
        ) AS total
    """)
    suspend fun count(keyword: String?, dateFrom: LocalDate?, dateTo: LocalDate?): ResultCount

    @Query("""
        SELECT tag, COUNT(*) as count, SUM(amount) as sum 
        FROM expenses 
        WHERE deleted = 0 
              AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
              OR (:dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo) 
        GROUP BY tag
    """)
    fun getDashboard(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<List<EntityExpensesAggrResult>>

    @Query("""
        SELECT DISTINCT tag 
        FROM expenses 
        WHERE tag IS NOT NULL AND deleted = 0 
        ORDER BY tag
    """)
    fun getTags(): LiveData<List<String>>

    @Query("""
        SELECT * 
        FROM expenses 
        WHERE id = :id
    """)
    suspend fun getExpenseFull(id: UUID): EntityExpenseFull?
}
