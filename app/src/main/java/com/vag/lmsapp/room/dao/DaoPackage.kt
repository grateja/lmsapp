package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.joborders.create.packages.MenuJobOrderPackage
import com.vag.lmsapp.room.entities.*
import java.util.*

@Dao
interface DaoPackage : BaseDao<EntityPackage> {
    @Query("SELECT * FROM packages WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    suspend fun get(id: UUID?): EntityPackage?

//    @Query("SELECT *, 1 as quantity FROM packages WHERE package_name LIKE '%' || :keyword || '%' AND deleted_at IS NULL")
    @Query("SELECT packages.id, packages.package_name," +
            "    COALESCE((" +
            "        SELECT SUM(products.price * package_products.quantity)" +
            "        FROM package_products" +
            "        JOIN products ON package_products.product_id = products.id" +
            "        WHERE package_products.package_id = packages.id AND package_products.deleted_at IS NULL" +
            "    ), 0) +" +
            "    COALESCE((" +
            "        SELECT SUM(services.price * package_services.quantity)" +
            "        FROM package_services" +
            "        JOIN services ON package_services.service_id = services.id" +
            "        WHERE package_services.package_id = packages.id AND package_services.deleted_at IS NULL" +
            "    ), 0) +" +
            "    COALESCE((" +
            "        SELECT SUM(extras.price * package_extras.quantity)" +
            "        FROM package_extras" +
            "        JOIN extras ON package_extras.extras_id = extras.id" +
            "        WHERE package_extras.package_id = packages.id AND package_extras.deleted_at IS NULL" +
            "    ), 0) AS total_price" +
            ", description, 1 as quantity FROM packages WHERE package_name LIKE '%' || :keyword || '%' AND deleted_at IS NULL")
    suspend fun getAll(keyword: String?): List<MenuJobOrderPackage>

    @Upsert
    suspend fun insertServices(packageServices: List<EntityPackageService>)

    @Query("DELETE FROM package_services WHERE deleted_at IS NOT NULL")
    fun clearServices()

    @Transaction
    suspend fun syncServices(packageServices: List<EntityPackageService>) {
        insertServices(packageServices)
        clearServices()
    }

    @Upsert
    suspend fun insertExtras(packageExtras: List<EntityPackageExtras>)

    @Query("DELETE FROM package_extras WHERE deleted_at IS NOT NULL")
    fun clearExtras()

    @Transaction
    suspend fun syncExtras(packageExtras: List<EntityPackageExtras>) {
        insertExtras(packageExtras)
        clearExtras()
    }

    @Upsert
    suspend fun insertProducts(packageProducts: List<EntityPackageProduct>)

    @Query("DELETE FROM package_products WHERE deleted_at IS NOT NULL")
    fun clearProducts()

    @Transaction
    suspend fun syncProducts(packageProducts: List<EntityPackageProduct>) {
        insertProducts(packageProducts)
        clearProducts()
    }

    @Query("SELECT * FROM packages WHERE id IN (:ids) AND deleted_at IS NULL")
    suspend fun getByIds(ids: List<UUID>?) : List<EntityPackageWithItems>

    @Query("SELECT *, " +
        "    COALESCE((" +
        "        SELECT SUM(products.price * package_products.quantity)" +
        "        FROM package_products" +
        "        JOIN products ON package_products.product_id = products.id" +
        "        WHERE package_products.package_id = packages.id AND package_products.deleted_at IS NULL" +
        "    ), 0) +" +
        "    COALESCE((" +
        "        SELECT SUM(services.price * package_services.quantity)" +
        "        FROM package_services" +
        "        JOIN services ON package_services.service_id = services.id" +
        "        WHERE package_services.package_id = packages.id AND package_services.deleted_at IS NULL" +
        "    ), 0) +" +
        "    COALESCE((" +
        "        SELECT SUM(extras.price * package_extras.quantity)" +
        "        FROM package_extras" +
        "        JOIN extras ON package_extras.extras_id = extras.id" +
        "        WHERE package_extras.package_id = packages.id AND package_extras.deleted_at IS NULL" +
        "    ), 0) AS total_price" +
        ", description, 1 as quantity FROM packages WHERE deleted_at IS NULL ORDER BY created_at DESC")
    fun getAllAsLiveData() : LiveData<List<EntityPackageWithItems>>

//    @Query("SELECT svs.*, psvs.quantity, 0 as used FROM services svs JOIN package_services psvs ON psvs.service_id = svs.id WHERE svs.id IN (:ids) AND deleted_at IS NULL")
//    abstract suspend fun getServicesByIds(ids: List<UUID>) : List<MenuServiceItem>

    @Query("SELECT * FROM packages WHERE id = :packageId AND deleted_at IS NULL")
    suspend fun getById(packageId: UUID?): EntityPackageWithItems?

    @Query("SELECT * FROM package_services WHERE package_id = :packageId")
    fun packageServicesAsLiveData(packageId: UUID?): LiveData<List<EntityPackageServiceWithService>>

    @Query("SELECT * FROM package_products WHERE package_id = :packageId")
    fun packageProductsAsLiveData(packageId: UUID?): LiveData<List<EntityPackageProductWithProduct>>

    @Query("SELECT * FROM package_extras WHERE package_id = :packageId")
    fun packageExtrasAsLiveData(packageId: UUID?): LiveData<List<EntityPackageExtrasWithExtras>>

    @Transaction
    suspend fun savePackage(packageWithItems: EntityPackageWithItems) {
        val id = packageWithItems.prePackage.id
        packageWithItems.services?.map {
            EntityPackageService(
                id,
                it.service.id,
                it.serviceCrossRef.quantity,
                it.serviceCrossRef.id,
                it.serviceCrossRef.deletedAt
            )
        }?.let {
            insertServices(it)
        }

        packageWithItems.products?.map {
            EntityPackageProduct(
                id,
                it.product.id,
                it.productCrossRef.quantity,
                it.productCrossRef.id,
                it.productCrossRef.deletedAt
            )
        }?.let {
            insertProducts(it)
        }

        packageWithItems.extras?.map {
            EntityPackageExtras(
                id,
                it.extras.id,
                it.extrasCrossRef.quantity,
                it.extrasCrossRef.id,
                it.extrasCrossRef.deletedAt
            )
        }?.let {
            insertExtras(it)
        }

        save(packageWithItems.prePackage)
    }

    @Query("SELECT * FROM packages WHERE id = :packageId")
    fun getAsLiveData(packageId: UUID?): LiveData<EntityPackage>

    @Query("SELECT * FROM packages WHERE id = :packageId")
    fun getPackageAsLiveData(packageId: UUID?) : LiveData<EntityPackageWithItems?>
}