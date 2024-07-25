package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.products.ProductAvailabilityChecker
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.app.products.list.ProductQueryResult
import com.vag.lmsapp.app.products.list.advanced_filter.ProductListAdvancedFilter
import com.vag.lmsapp.app.products.preview.ProductPreview
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.util.ResultCount
import java.util.UUID

@Dao
abstract class DaoProduct : BaseDao<EntityProduct> {
    @Query("SELECT * FROM products WHERE id = :id")
    abstract suspend fun get(id: UUID): EntityProduct?

    @Query("SELECT * FROM products")
    abstract suspend fun getAll(): List<EntityProduct>

    @Query("SELECT *, 1 as quantity, 0 as void, 0 as discounted_price FROM products WHERE deleted = 0")
    abstract suspend fun menuItems(): List<MenuProductItem>

    @Query("""
        SELECT *
        FROM products 
        WHERE name LIKE '%' || :keyword || '%' 
            AND deleted = 0 
        ORDER BY
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'ASC' THEN name END ASC,
            CASE WHEN :orderBy = 'Current Stock' AND :sortDirection = 'ASC' THEN current_stock END ASC,
            CASE WHEN :orderBy = 'Price' AND :sortDirection = 'ASC' THEN price END ASC,
            CASE WHEN :orderBy = 'Product Type' AND :sortDirection = 'ASC' THEN product_type END ASC,
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'DESC' THEN name END DESC,
            CASE WHEN :orderBy = 'Current Stock' AND :sortDirection = 'DESC' THEN current_stock END DESC,
            CASE WHEN :orderBy = 'Price' AND :sortDirection = 'DESC' THEN price END DESC,
            CASE WHEN :orderBy = 'Product Type' AND :sortDirection = 'DESC' THEN product_type END DESC
        LIMIT 20 OFFSET :offset
    """)
    abstract suspend fun filter(keyword: String, offset: Int, orderBy: String?, sortDirection: String?): List<ProductItemFull>

    @Query("")
    suspend fun queryResult(keyword: String, offset: Int, advancedFilter: ProductListAdvancedFilter?): ProductQueryResult {
        return ProductQueryResult(
            filter(keyword, offset, advancedFilter?.orderBy, advancedFilter?.sortDirection?.toString()),
            count(keyword)
        )
    }

    @Query("""SELECT
        (
            SELECT COUNT(*)
            FROM products
            WHERE name LIKE '%' || :keyword || '%'
                AND deleted = 0
        ) AS filtered, (
            SELECT COUNT(*)
            FROM products
            WHERE deleted = 0
        ) AS total
    """)
    abstract suspend fun count(keyword: String): ResultCount

    @Query("""
        SELECT *, current_stock - (
            COALESCE((
                SELECT (:newQuantity - quantity) FROM job_order_products WHERE id = :joProductId
            ), :newQuantity) * unit_per_serve
        ) as available FROM products WHERE id = :productId AND available < 0
    """)
    abstract suspend fun checkAvailability(productId: UUID, joProductId: UUID?, newQuantity: Int): EntityProduct?

    suspend fun checkAll(products: List<ProductAvailabilityChecker>): String? {
        val unavailable = mutableListOf<String>()
        products.forEach {
            checkAvailability(it.productId, it.joProductItemId, it.quantity)?.let { product ->
                product.name.let { name ->
                    unavailable.add(name)
                }
            }
        }
        return if (unavailable.isNotEmpty()) {
            "No available stocks for: ${
                unavailable.joinToString(", ", transform = { productName ->
                    if (unavailable.size > 1 && productName == unavailable.last()) {
                        "and $productName"
                    } else {
                        productName
                    }
                })
            }."
        } else null
    }

    @Query("SELECT * FROM products WHERE id = :productId AND deleted = 0")
    abstract fun getProductPreviewAsLiveData(productId: UUID): LiveData<ProductPreview>

    @Query("SELECT * FROM products WHERE id = :productId AND deleted = 0")
    abstract fun getAsLiveData(productId: UUID?): LiveData<EntityProduct?>

    @Query("SELECT * FROM products WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityProduct>

    @Query("UPDATE products SET hidden = CASE WHEN hidden = 1 THEN 0 ELSE 1 END WHERE id = :productId")
    abstract suspend fun hideToggle(productId: UUID?)
}
