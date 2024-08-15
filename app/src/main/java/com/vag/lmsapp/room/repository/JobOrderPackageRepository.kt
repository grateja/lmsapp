package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.packages.MenuJobOrderPackage
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.dao.DaoPackage
import com.vag.lmsapp.room.entities.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobOrderPackageRepository

@Inject
constructor(private val daoPackage: DaoPackage) : BaseRepository<EntityPackage>(daoPackage) {
    override suspend fun get(id: UUID?): EntityPackage? {
        return daoPackage.get(id)
    }

    suspend fun getAll(keyword: String?): List<MenuJobOrderPackage> {
        return daoPackage.getAll(keyword)
    }

    fun getAsLiveData(packageId: UUID?) = daoPackage.getAsLiveData(packageId)

    suspend fun getPackageServicesByPackageId(packageId: UUID) = daoPackage.getPackageServicesByPackageId(packageId)
    suspend fun getPackageProductsByPackageId(packageId: UUID) = daoPackage.getPackageProductsByPackageId(packageId)
    suspend fun getPackageExtrasByPackageId(packageId: UUID) = daoPackage.getPackageExtrasByPackageId(packageId)

    suspend fun getByIds(ids: List<UUID>?): List<EntityPackageWithItems> {
        return daoPackage.getByIds(ids)
    }

    suspend fun getById(packageId: UUID?): EntityPackageWithItems? {
        return daoPackage.getById(packageId)
    }

    fun getAllAsLiveData() = daoPackage.getAllAsLiveData()

    suspend fun filter(keyword: String?) = daoPackage.filter(keyword)

    suspend fun syncServices(services: List<EntityPackageService>) {
        daoPackage.syncServices(services)
    }

    suspend fun syncProducts(products: List<EntityPackageProduct>) {
        daoPackage.syncProducts(products)
    }

    suspend fun syncExtras(extras: List<EntityPackageExtras>) {
        daoPackage.syncExtras(extras)
    }

//    fun getPackageServicesAsLiveData(packageId: UUID?) = daoPackage.packageServicesAsLiveData(packageId)
//    fun getPackageProductsAsLiveData(packageId: UUID?) = daoPackage.packageProductsAsLiveData(packageId)
//    fun getPackageExtrasAsLiveData(packageId: UUID?) = daoPackage.packageExtrasAsLiveData(packageId)
//
//    fun getAsLiveData(packageId: UUID?) : LiveData<EntityPackage> {
//        return daoPackage.getAsLiveData(packageId)
//    }

    fun getPackageWithItemsAsLiveData(packageId: UUID?) = daoPackage.getPackageAsLiveData(packageId)

    suspend fun saveAll(packageWithItems: EntityPackageWithItems) = daoPackage.saveAll(packageWithItems)
    suspend fun hideToggle(packageId: UUID?) = daoPackage.hideToggle(packageId)
}