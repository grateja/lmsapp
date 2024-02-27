package com.vag.lmsapp.app.expenses

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityUser

data class ExpenseItemFull(
    @Embedded
    val expense: EntityExpense,

    @Relation(
        parentColumn = "created_by",
        entityColumn = "id"
    )
    val createdBy: EntityUser,

    @Relation(
        parentColumn = "deleted_by",
        entityColumn = "id"
    )
    val deletedBy: EntityUser?,
)