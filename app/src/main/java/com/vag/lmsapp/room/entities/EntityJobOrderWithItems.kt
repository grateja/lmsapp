package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.squareup.moshi.Json
import com.vag.lmsapp.util.isNotEmpty

data class EntityJobOrderWithItems (
    @Embedded var jobOrder: EntityJobOrder,

    @Relation(
        parentColumn = "id",
        entityColumn = "job_order_id"
    )
    var packages: List<EntityJobOrderPackage>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "job_order_id"
    )
    var services: List<EntityJobOrderService>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "job_order_id"
    )
    var extras: List<EntityJobOrderExtras>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "job_order_id"
    )
    var products: List<EntityJobOrderProduct>?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    var deliveryCharge: EntityJobOrderDeliveryCharge? = null,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    var discount: EntityJobOrderDiscount? = null,
) {
    @Relation(
        parentColumn = "customer_id",
        entityColumn = "id"
    )
    var customer: EntityCustomer? = null

    @Json(name = "staff")
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    var user: EntityUser? = null

    @Relation(
        parentColumn = "payment_id",
        entityColumn = "id",
        entity = EntityJobOrderPayment::class
    )
    var paymentWithUser: EntityJobOrderPaymentFull? = null

    fun hasRemarks(): Boolean {
        return jobOrder.remarks != null && jobOrder.remarks != ""
    }

//    fun servicesTotal() : Float {
//        return services?.filter { !it.deleted }?.let {
//            var result = 0f
//            if(it.isNotEmpty()) {
//                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
//                    sum + element
//                }
//            }
//            result
//        } ?: 0f
//    }
//
//    fun productsTotal() : Float {
//        return products?.filter { !it.deleted }?.let {
//            var result = 0f
//            if(it.isNotEmpty()) {
//                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
//                    sum + element
//                }
//            }
//            result
//        } ?: 0f
//    }
//
//    fun extrasTotal() : Float {
//        return extras?.filter { !it.deleted }?.let {
//            var result = 0f
//            if(it.isNotEmpty()) {
//                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
//                    sum + element
//                }
//            }
//            result
//        } ?: 0f
//    }
//
//    fun deliveryFee() : Float {
//        return deliveryCharge?.price?:0f
//    }
//
//    fun subtotal() : Float {
//        return servicesTotal() + productsTotal() + extrasTotal() + deliveryFee()
//    }
//
//    fun discountInPeso() : Float {
//        return discount?.let {
//            if(it.deleted) return@let 0f
//            var total = 0f
//            total += it.getDiscount(servicesTotal(), EnumDiscountApplicable.WASH_DRY_SERVICES)
//            total += it.getDiscount(productsTotal(), EnumDiscountApplicable.PRODUCTS_CHEMICALS)
//            total += it.getDiscount(extrasTotal(), EnumDiscountApplicable.EXTRAS)
//            total += it.getDiscount(deliveryFee(), EnumDiscountApplicable.DELIVERY)
//            total
//        } ?: 0f
//    }
//
//    fun discountedAmount() : Float {
//        return subtotal() - discountInPeso()
//    }
}