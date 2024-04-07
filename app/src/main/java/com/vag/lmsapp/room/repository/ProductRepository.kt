package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.room.dao.DaoProduct
import com.vag.lmsapp.room.entities.EntityProduct
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository
@Inject
constructor (
    private val daoProduct: DaoProduct,
) : BaseRepository<EntityProduct> (daoProduct) {
    override suspend fun get(id: UUID?) : EntityProduct? {
        if(id == null) return null

        return daoProduct.get(id)
    }

    suspend fun getAll() : List<EntityProduct> {
        return daoProduct.getAll()
    }

    suspend fun menuItems() : List<MenuProductItem> {
        return daoProduct.menuItems()
    }

    suspend fun filter(keyword: String) : List<ProductItemFull> {
        return daoProduct.filter(keyword)
    }

    suspend fun checkAll(products: List<MenuProductItem>) : String? {
        return daoProduct.checkAll(products)
    }

    fun getProductPreviewAsLiveData(productId: UUID) = daoProduct.getProductPreviewAsLiveData(productId)
    fun getProductAsLiveData(productId: UUID?) = daoProduct.getAsLiveData(productId)
}