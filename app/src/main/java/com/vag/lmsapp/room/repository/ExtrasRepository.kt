package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.app.extras.ExtrasItemFull
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
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

    suspend fun getAll(keyword: String) : List<MenuExtrasItem> {
        return daoExtras.getAll(keyword)
    }

    suspend fun filter(keyword: String) : List<ExtrasItemFull> {
        return daoExtras.filter(keyword)
    }

    fun getCategories(): LiveData<List<String>> = daoExtras.getCategories()

    suspend fun unSynced(forced: Boolean) = daoExtras.unSynced(forced)
}