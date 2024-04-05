package com.vag.lmsapp.room.dao

import androidx.room.*
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.room.entities.EntityProduct
import java.util.UUID

@Dao
abstract class DaoProduct : BaseDao<EntityProduct> {
    @Query("SELECT * FROM products WHERE id = :id")
    abstract suspend fun get(id: UUID) : EntityProduct?

    @Query("SELECT * FROM products")
    abstract suspend fun getAll() : List<EntityProduct>

    @Query("SELECT *, 1 as quantity, 0 as void FROM products WHERE deleted_at IS NULL")
    abstract suspend fun menuItems(): List<MenuProductItem>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' AND deleted_at IS NULL ORDER BY name")
    abstract suspend fun filter(keyword: String): List<ProductItemFull>

    @Query("SELECT *, current_stock - (COALESCE((SELECT (:newQuantity - quantity) FROM job_order_products WHERE id = :joProductId), :newQuantity) * unit_per_serve) as available FROM products WHERE id = :productId AND available < 0")
    abstract suspend fun checkAvailability(productId: UUID, joProductId: UUID?, newQuantity: Float) : EntityProduct?

    suspend fun checkAll(products: List<MenuProductItem>) : String? {
        val unavailable = mutableListOf<String>()
        products.forEach {
            checkAvailability(it.productRefId, it.joProductItemId, it.quantity)?.let {
                it.name?.let {
                    unavailable.add(it)
                }
            }
        }
//        for ((productId, quantity) in products) {
//            val product = checkAvailability(productId, quantity)
//            if(product != null) {
//                unavailable.add(product.name.toString())
//            }
//        }
        return if(unavailable.isNotEmpty()) {
            "No available stocks for: ${
                unavailable.joinToString(", ", transform = { productName ->
                    if (unavailable.size > 1 && productName == unavailable.last()) {
                        "and $productName"
                    } else {
                        productName
                    }
                })
            }."
//            "No available stocks for: ${unavailable.joinToString(", ")}."
        } else null
    }
}