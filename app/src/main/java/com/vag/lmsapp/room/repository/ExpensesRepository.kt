package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.expenses.ExpenseItemFull
import com.vag.lmsapp.room.dao.DaoExpense
import com.vag.lmsapp.room.entities.EntityExpense
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpensesRepository
@Inject
constructor (
    private val dao: DaoExpense,
) : BaseRepository<EntityExpense>(dao) {
    override suspend fun get(id: UUID?) : EntityExpense? {
        if(id == null) return null
        return dao.get(id)
    }

    suspend fun filter(keyword: String, dateFilter: DateFilter?): List<ExpenseItemFull> {
        return dao.getAll(keyword, dateFilter?.dateFrom, dateFilter?.dateTo)
    }

    fun getDashboard(dateFilter: DateFilter) = dao.getDashboard(dateFilter.dateFrom, dateFilter.dateTo)

    fun getTags() = dao.getTags()
}