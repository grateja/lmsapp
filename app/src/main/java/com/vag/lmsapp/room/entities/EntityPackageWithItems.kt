package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.app.packages.EnumPackageItemType
import com.vag.lmsapp.app.packages.list.PackageItem

//data class EntityPackageServiceWithService(
//    @Embedded
//    val serviceCrossRef: EntityPackageService,
//
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id",
//        entity = EntityService::class
//    )
//    val service: EntityService
//)
//
//data class EntityPackageProductWithProduct(
//    @Embedded
//    val productCrossRef: EntityPackageProduct,
//
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id",
//        entity = EntityProduct::class
//    )
//    val product: EntityProduct
//)
//
//data class EntityPackageExtrasWithExtras(
//    @Embedded
//    val extrasCrossRef: EntityPackageExtras,
//
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id",
//        entity = EntityExtras::class
//    )
//    val extras: EntityExtras
//)

data class EntityPackageWithItems(
    @Embedded
    val prePackage: EntityPackage,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
    )
    val services: List<EntityPackageService>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
    )
    val extras: List<EntityPackageExtras>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "package_id",
    )
    val products: List<EntityPackageProduct>?,
) {
    fun simpleList() : List<PackageItem> {
        val list: MutableList<PackageItem> = mutableListOf()

        services?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    EnumPackageItemType.WASH_DRY,
                    it.id,
                    it.label(),
                    it.serviceRef.description(),
                    it.unitPrice,
                    it.quantity,
                    it.deleted,
                )
            }.toList())
        }

        products?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    EnumPackageItemType.PRODUCTS,
                    it.id,
                    it.productName,
                    it.toString(),
                    it.unitPrice,
                    it.quantity,
                    it.deleted,
                )
            }.toList())
        }

        extras?.let { items ->
            list.addAll(items.map {
                PackageItem(
                    EnumPackageItemType.EXTRAS,
                    it.id,
                    it.extrasName,
                    "",
                    it.unitPrice,
                    it.quantity,
                    it.deleted,
                )
            }.toList())
        }

        return list.filter { !it.deleted }
    }
}