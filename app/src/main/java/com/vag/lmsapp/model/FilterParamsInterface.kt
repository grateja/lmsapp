package com.vag.lmsapp.model

import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.util.EnumSortDirection

interface FilterParamsInterface {
    var orderBy: String
    var sortDirection: EnumSortDirection
    var dateFilter: DateFilter?
}