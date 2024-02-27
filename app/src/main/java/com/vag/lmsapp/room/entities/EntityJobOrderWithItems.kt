package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.model.EnumDiscountApplicable
import com.vag.lmsapp.model.EnumDiscountType

data class EntityJobOrderWithItems (
    @Embedded var jobOrder: EntityJobOrder,

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

//    @Relation(
//        parentColumn = "payment_id",
//        entityColumn = "id"
//    )
//    var payment: EntityJobOrderPayment? = null

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

    fun servicesTotal() : Float {
        return services?.filter { it.deletedAt == null }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    fun productsTotal() : Float {
        return products?.filter { it.deletedAt == null }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    fun extrasTotal() : Float {
        return extras?.filter { it.deletedAt == null }?.let {
            var result = 0f
            if(it.isNotEmpty()) {
                result = it.map { s -> s.price * s.quantity } .reduce { sum, element ->
                    sum + element
                }
            }
            result
        } ?: 0f
    }

    fun deliveryFee() : Float {
        return deliveryCharge?.price?:0f
    }

    fun subtotal() : Float {
        return servicesTotal() + productsTotal() + extrasTotal() + deliveryFee()
    }

    fun discountInPeso() : Float {
        return discount?.let {
            if(it.deletedAt != null) return@let 0f
            if(it.discountType == EnumDiscountType.FIXED) return@let it.value
            var total = 0f
            total += it.getDiscount(servicesTotal(), EnumDiscountApplicable.WASH_DRY_SERVICES)
            total += it.getDiscount(productsTotal(), EnumDiscountApplicable.PRODUCTS_CHEMICALS)
            total += it.getDiscount(extrasTotal(), EnumDiscountApplicable.EXTRAS)
            total += it.getDiscount(deliveryFee(), EnumDiscountApplicable.DELIVERY)
            total
        } ?: 0f
    }
}