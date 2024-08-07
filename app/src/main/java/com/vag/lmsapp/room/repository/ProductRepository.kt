package com.vag.lmsapp.room.repository

import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.products.ProductAvailabilityChecker
import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.app.products.list.ProductQueryResult
import com.vag.lmsapp.app.products.list.advanced_filter.ProductListAdvancedFilter
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

    suspend fun menuItems() = daoProduct.menuItems()

    suspend fun filter(keyword: String, page: Int, advancedFilter: ProductListAdvancedFilter?) : ProductQueryResult {
        val offset = (20 * page) - 20
        return daoProduct.queryResult(keyword, offset, advancedFilter)
    }

    suspend fun checkAll(products: List<ProductAvailabilityChecker>) : String? {
        return daoProduct.checkAll(products)
    }

    fun getProductPreviewAsLiveData(productId: UUID) = daoProduct.getProductPreviewAsLiveData(productId)
    fun getProductAsLiveData(productId: UUID?) = daoProduct.getAsLiveData(productId)

    suspend fun unSynced(forced: Boolean) = daoProduct.unSynced(forced)

    suspend fun hideToggle(productId: UUID?) = daoProduct.hideToggle(productId)
}