package com.vag.lmsapp.app.products.preview

import androidx.room.Embedded
import androidx.room.Relation
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.room.entities.EntityProduct

data class ProductPreview(
    @Embedded
    val product: EntityProduct
)
