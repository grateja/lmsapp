package com.vag.lmsapp.util

sealed class FilterState<out R> {
    data object StateLess: FilterState<Nothing>()
    data class LoadItems<R>(val items: List<R>, val reset: Boolean) : FilterState<R>()
}