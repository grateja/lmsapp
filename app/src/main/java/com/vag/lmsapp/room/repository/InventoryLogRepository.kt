package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.products.preview.inventory_in.InventoryLogAdvancedFilter
import com.vag.lmsapp.room.dao.DaoInventoryLog
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityInventoryLog
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.util.QueryResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryLogRepository

@Inject
constructor(
    private val dao: DaoInventoryLog
): BaseRepository<EntityInventoryLog>(dao) {
    override suspend fun get(id: UUID?): EntityInventoryLog? {
        if(id == null) return null
        return dao.get(id)
    }

    suspend fun save(inventoryLog: EntityInventoryLog, expense: EntityExpense?) = dao.save(inventoryLog, expense)

    suspend fun getInventoryLogFull(id: UUID) = dao.getInventoryLogFull(id)

    suspend fun queryResult(keyword: String?, filter: InventoryLogAdvancedFilter?, page: Int): QueryResult<EntityInventoryLogFull> {
        val offset = (20 * page) - 20
        return dao.queryResult(keyword ?: "", filter, offset)
    }
}