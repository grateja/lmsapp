package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.joborders.create.packages.MenuJobOrderPackage
import com.vag.lmsapp.room.entities.*
import java.util.*

@Dao
interface DaoPackage : BaseDao<EntityPackage> {
    @Query("SELECT * FROM packages WHERE id = :id AND deleted = 0 LIMIT 1")
    suspend fun get(id: UUID?): EntityPackage?

    @Query("""
        SELECT packages.id, packages.package_name, total_price, description, 0 as void, 0 AS discounted_price, 1 as quantity, 0 as deleted FROM packages WHERE package_name LIKE '%' || :keyword || '%' AND deleted = 0
    """)
    suspend fun getAll(keyword: String?): List<MenuJobOrderPackage>

    @Query("""
        SELECT * FROM package_services WHERE package_id = :packageId AND deleted = 0
    """)
    suspend fun getPackageServicesByPackageId(packageId: UUID): List<EntityPackageService>

    @Query("""
        SELECT * FROM package_products WHERE package_id = :packageId AND deleted = 0
    """)
    suspend fun getPackageProductsByPackageId(packageId: UUID): List<EntityPackageProduct>

    @Query("""
        SELECT * FROM package_extras WHERE package_id = :packageId AND deleted = 0
    """)
    suspend fun getPackageExtrasByPackageId(packageId: UUID): List<EntityPackageExtras>

    @Upsert
    suspend fun insertServices(packageServices: List<EntityPackageService>)

    @Query("DELETE FROM package_services WHERE deleted = 0")
    fun clearServices()

    @Transaction
    suspend fun syncServices(packageServices: List<EntityPackageService>) {
        insertServices(packageServices)
        clearServices()
    }

    @Upsert
    suspend fun insertExtras(packageExtras: List<EntityPackageExtras>)

    @Query("DELETE FROM package_extras WHERE deleted = 0")
    fun clearExtras()

    @Transaction
    suspend fun syncExtras(packageExtras: List<EntityPackageExtras>) {
        insertExtras(packageExtras)
        clearExtras()
    }

    @Upsert
    suspend fun insertProducts(packageProducts: List<EntityPackageProduct>)

    @Query("DELETE FROM package_products WHERE deleted = 0")
    fun clearProducts()

    @Transaction
    suspend fun syncProducts(packageProducts: List<EntityPackageProduct>) {
        insertProducts(packageProducts)
        clearProducts()
    }

    @Query("SELECT * FROM packages WHERE id IN (:ids) AND deleted = 0")
    suspend fun getByIds(ids: List<UUID>?): List<EntityPackageWithItems>

    @Query("""
        SELECT *, 1 as quantity FROM packages WHERE deleted = 0 ORDER BY created_at DESC
    """)
    fun getAllAsLiveData(): LiveData<List<EntityPackageWithItems>>

    @Query("SELECT * FROM packages WHERE id = :packageId AND deleted = 0")
    suspend fun getById(packageId: UUID?): EntityPackageWithItems?

//    @Query("SELECT * FROM package_services WHERE package_id = :packageId")
//    fun packageServicesAsLiveData(packageId: UUID?): LiveData<List<EntityPackageServiceWithService>>
//
//    @Query("SELECT * FROM package_products WHERE package_id = :packageId")
//    fun packageProductsAsLiveData(packageId: UUID?): LiveData<List<EntityPackageProductWithProduct>>
//
//    @Query("SELECT * FROM package_extras WHERE package_id = :packageId")
//    fun packageExtrasAsLiveData(packageId: UUID?): LiveData<List<EntityPackageExtrasWithExtras>>

    @Query("SELECT * FROM packages WHERE id = :packageId")
    fun getAsLiveData(packageId: UUID?): LiveData<EntityPackage>

    @Query("SELECT * FROM packages WHERE id = :packageId")
    fun getPackageAsLiveData(packageId: UUID?): LiveData<EntityPackageWithItems?>

    @Transaction
    suspend fun saveAll(packageWithItems: EntityPackageWithItems) {
        save(packageWithItems.prePackage)
        packageWithItems.services?.let {
            insertServices(it)
        }
        packageWithItems.products?.let {
            insertProducts(it)
        }
        packageWithItems.extras?.let {
            insertExtras(it)
        }
    }
}
