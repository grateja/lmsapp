package com.vag.lmsapp.util

interface FilterActivityInterface {
//    var toolbarBackground: Int
    var filterHint: String
    var enableAdvancedFilter: Boolean
//    var enableAddButton: Boolean
    fun onQuery(keyword: String?)
    fun onAdvancedSearchClick() { }
//    fun onAddButtonClick() { }
}