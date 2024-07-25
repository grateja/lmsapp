package com.vag.lmsapp.util

import com.vag.lmsapp.model.BaseFilterParams

sealed class FilterState<out R, B> {
    class StateLess<R, B>: FilterState<R, B>()
    data class LoadItems<R, B: BaseFilterParams>(val items: List<R>, val reset: Boolean) : FilterState<R, B>()
    data class ShowAdvancedFilter<out R, B: BaseFilterParams>(val advancedFilter: B): FilterState<R, B>()
}