package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityInventoryLog
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
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
