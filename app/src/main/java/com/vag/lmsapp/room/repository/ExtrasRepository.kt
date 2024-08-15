package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.extras.list.ExtrasQueryResult
import com.vag.lmsapp.app.extras.list.advanced_filter.ExtrasAdvancedFilter
import com.vag.lmsapp.room.dao.DaoExtras
//import com.csi.palabakosys.room.dao.DaoOtherService
import com.vag.lmsapp.room.entities.EntityExtras
import java.util.UUID
//import com.csi.palabakosys.room.entities.EntityServiceOther
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtrasRepository
@Inject
constructor (
    private val daoExtras: DaoExtras,
) : BaseRepository<EntityExtras>(daoExtras) {
    override suspend fun get(id: UUID?) : EntityExtras? {
        if(id == null) return null
        return daoExtras.get(id)
    }

    suspend fun menuItems() = daoExtras.menuItems()

    suspend fun filter(keyword: String?, page: Int, advancedFilter: ExtrasAdvancedFilter) : ExtrasQueryResult {
        val offset = (20 * page) - 20
        return daoExtras.queryResult(keyword ?: "", offset, advancedFilter)
    }

    fun getCategories(): LiveData<List<String>> = daoExtras.getCategories()

    suspend fun unSynced(forced: Boolean) = daoExtras.unSynced(forced)

    fun getAsLiveData(extrasId: UUID) = daoExtras.getAsLiveData(extrasId)

    suspend fun hideToggle(extrasId: UUID) = daoExtras.hideToggle(extrasId)
}