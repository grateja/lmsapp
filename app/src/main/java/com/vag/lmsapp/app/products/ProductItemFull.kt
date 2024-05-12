package com.vag.lmsapp.app.products

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.room.entities.EntityUser

data class ProductItemFull(
    @Embedded
    val product: EntityProduct
)