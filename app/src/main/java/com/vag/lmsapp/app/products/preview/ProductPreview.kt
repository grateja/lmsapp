package com.vag.lmsapp.app.products.preview

import androidx.room.Embedded
import com.vag.lmsapp.room.entities.EntityProduct

data class ProductPreview(
    @Embedded
    val product: EntityProduct
)
