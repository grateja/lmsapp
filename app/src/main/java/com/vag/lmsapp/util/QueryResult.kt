package com.vag.lmsapp.util

data class QueryResult<T> (
    val result: List<T>,
    val resultCount: ResultCount?
)