package com.vag.lmsapp.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EntityExpenseFull(
    @Embedded
    val expense: EntityExpense,

    @Relation(
        parentColumn = "created_by",
        entityColumn = "id"
    )
    val createdBy: EntityUser
)
