package com.vag.lmsapp.app.extras.list.advanced_filter

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.util.EnumSortDirection
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtrasAdvancedFilter(
    override var orderBy: String = "Name",
) : Parcelable, BaseFilterParams(
    sortDirection = EnumSortDirection.ASC
)