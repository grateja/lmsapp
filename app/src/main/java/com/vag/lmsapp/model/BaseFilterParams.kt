package com.vag.lmsapp.model

import android.os.Parcelable
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.EnumSortDirection
import kotlinx.parcelize.Parcelize

@Parcelize
open class BaseFilterParams(
    open var orderBy: String = "Date Created",
    var sortDirection: EnumSortDirection = EnumSortDirection.DESC,
    open var dateFilter: DateFilter? = null
) : Parcelable
