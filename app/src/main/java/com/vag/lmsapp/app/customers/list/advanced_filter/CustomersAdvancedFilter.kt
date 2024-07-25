package com.vag.lmsapp.app.customers.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.util.EnumSortDirection
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomersAdvancedFilter(
    var hideAllWithoutJo: Boolean = false,
) : Parcelable, BaseFilterParams(
    sortDirection = EnumSortDirection.ASC,
    orderBy = "Name"
)