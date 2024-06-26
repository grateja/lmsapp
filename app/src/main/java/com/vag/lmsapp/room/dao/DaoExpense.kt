package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.expenses.ExpenseItemFull
import com.vag.lmsapp.room.entities.EntityExpensesAggrResult
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityExpenseFull
import java.time.LocalDate
import java.util.UUID

@Dao
abstract class DaoExpense : BaseDao<EntityExpense> {

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
        ORDER BY remarks
    """)
    abstract suspend fun getAll(keyword: String, dateFrom: LocalDate?, dateTo: LocalDate?): List<ExpenseItemFull>

    @Query("""
        SELECT tag, COUNT(*) as count, SUM(amount) as sum 
        FROM expenses 
        WHERE deleted = 0 
              AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom 
              OR (:dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo) 
        GROUP BY tag
    """)
    abstract fun getDashboard(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<List<EntityExpensesAggrResult>>

    @Query("""
        SELECT DISTINCT tag 
        FROM expenses 
        WHERE tag IS NOT NULL AND deleted = 0 
        ORDER BY tag
    """)
    abstract fun getTags(): LiveData<List<String>>

    @Query("""
        SELECT * 
        FROM expenses 
        WHERE id = :id
    """)
    abstract suspend fun getExpenseFull(id: UUID): EntityExpenseFull?
}
