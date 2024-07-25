package com.vag.lmsapp.app.products.list

import com.vag.lmsapp.app.products.ProductItemFull
import com.vag.lmsapp.util.ResultCount

data class ProductQueryResult(
    val items: List<ProductItemFull>,
    val resultCount: ResultCount
)
