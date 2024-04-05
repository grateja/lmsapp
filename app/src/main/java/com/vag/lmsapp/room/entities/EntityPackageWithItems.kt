package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.app.packages.PackageItem

data class EntityPackageServiceWithService(
    @Embedded
    val serviceCrossRef: EntityPackageService,

    @Relation(
        parentColumn = "service_id",
        entityColumn = "id",
        entity = EntityService::class
    )
    val service: EntityService
)

data class EntityPackageProductWithProduct(
    @Embedded
    val productCrossRef: EntityPackageProduct,

    @Relation(
        parentColumn = "product_id",
        entityColumn = "id",
        entity = EntityProduct::class
    )
    val product: EntityProduct
)

data class EntityPackageExtrasWithExtras(
    @Embedded
    val extrasCrossRef: EntityPackageExtras,

    @Relation(
        parentColumn = "extras_id",
        entityColumn = "id",
        entity = EntityExtras::class
    )
    val extras: EntityExtras
)

data class EntityPackageWithItems(
    @Embedded
    val prePackage: EntityPackage,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
        entity = EntityPackageService::class
    )
    val services: List<EntityPackageServiceWithService>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
        entity = EntityPackageExtras::class
    )
    val extras: List<EntityPackageExtrasWithExtras>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
        entity = EntityPackageProduct::class
    )
    val products: List<EntityPackageProductWithProduct>?,
) {
    fun simpleList() : List<PackageItem> {
        val list: MutableList<PackageItem> = mutableListOf()

        services?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    it.service.id,
                    it.service.name.toString(),
                    it.service.price,
                    it.serviceCrossRef.quantity.toFloat(),
                    it.serviceCrossRef.deletedAt,
                )
            }.toList())
        }

        products?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    it.product.id,
                    it.product.name.toString(),
                    it.product.price,
                    it.productCrossRef.quantity,
                    it.productCrossRef.deletedAt,
                )
            }.toList())
        }

        extras?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    it.extras.id,
                    it.extras.name.toString(),
                    it.extras.price,
                    it.extrasCrossRef.quantity.toFloat(),
                    it.extrasCrossRef.deletedAt,
                )
            }.toList())
        }

        return list.filter { it.deletedAt == null }
    }
}