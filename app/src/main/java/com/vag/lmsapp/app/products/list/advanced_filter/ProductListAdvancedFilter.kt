package com.vag.lmsapp.app.products.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductListAdvancedFilter(
    override var orderBy: String = "Name"
): Parcelable, BaseFilterParams()
