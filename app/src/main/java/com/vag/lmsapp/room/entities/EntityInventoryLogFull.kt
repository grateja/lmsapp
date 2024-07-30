package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EntityInventoryLogFull(
    @Embedded
    val inventoryLog: EntityInventoryLog,

    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: EntityProduct,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val staff: EntityUser,

    @Relation(
        parentColumn = "expense_id",
        entityColumn = "id"
    )
    val expense: EntityExpense? = null
) {
    private fun serving() : String {
        return if(product.unitPerServe == 1f) { "" } else {
            "${product.unitPerServe}"
        } + "${product.measureUnit}"
    }

    fun quantityStr() : String {
        return "+${inventoryLog.quantity} " + serving()
    }
}