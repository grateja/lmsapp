package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vag.lmsapp.app.products.preview.inventory_in.InventoryLogAdvancedFilter
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityInventoryLog
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.util.QueryResult
import com.vag.lmsapp.util.ResultCount
import java.time.LocalDate
import java.util.UUID

@Dao
interface DaoInventoryLog : BaseDao<EntityInventoryLog> {

    @Query("""
        SELECT * 
        FROM inventory_log 
        WHERE id = :id AND deleted = 0
    """)
    suspend fun get(id: UUID): EntityInventoryLog?

    @Query("""
        SELECT il.*, prd.name 
        FROM inventory_log il 
        JOIN products prd ON prd.id = il.product_id 
        WHERE prd.name LIKE '%' || :keyword || '%' AND il.deleted = 0 
        ORDER BY prd.name
    """)
    suspend fun getAll(keyword: String): List<EntityInventoryLog>

    @Query("""
        SELECT * 
        FROM inventory_log 
        WHERE remarks LIKE '%' || :keyword || '%' AND deleted = 0
            AND (product_id = :productId OR :productId IS NULL)
            AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
        ORDER BY created_at DESC
        LIMIT 20 OFFSET :offset
    """)
    suspend fun filter(productId: UUID?, keyword: String, dateFrom: LocalDate?, dateTo: LocalDate?, offset: Int) : List<EntityInventoryLogFull>

    @Query("""SELECT(
        SELECT COUNT(*) 
        FROM inventory_log 
        WHERE remarks LIKE '%' || :keyword || '%' AND deleted = 0
            AND (product_id = :productId OR :productId IS NULL)
            AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NULL AND date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR 
                (:dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo))
    ) AS filtered, (
        SELECT COUNT(*) 
        FROM inventory_log 
        WHERE deleted = 0 AND (product_id = :productId OR :productId IS NULL)
    ) AS total
    """)
    suspend fun resultCount(productId: UUID?, keyword: String, dateFrom: LocalDate?, dateTo: LocalDate?): ResultCount

    suspend fun queryResult(keyword: String, filter: InventoryLogAdvancedFilter?, offset: Int): QueryResult<EntityInventoryLogFull> {
        return QueryResult(
            filter(filter?.productId, keyword, filter?.dateFilter?.dateFrom, filter?.dateFilter?.dateTo, offset),
            resultCount(filter?.productId, keyword, filter?.dateFilter?.dateFrom, filter?.dateFilter?.dateTo)
        )
    }

    @Transaction
    suspend fun save(data: EntityInventoryLog, expense: EntityExpense?) {
        data.expenseId = expense?.id
        save(data)
        updateStock(data.productId!!, data.quantity)
        expense?.let {
            saveAsExpense(expense)
        }
    }

    @Query("""
        UPDATE products 
        SET current_stock = current_stock + :additionalStock 
        WHERE id = :productId
    """)
    suspend fun updateStock(productId: UUID, additionalStock: Float)

    @Upsert
    suspend fun saveAsExpense(expense: EntityExpense)

    @Query("""
        SELECT * 
        FROM inventory_log 
        WHERE id = :id
    """)
    suspend fun getInventoryLogFull(id: UUID): EntityInventoryLogFull?
}
