package com.vag.lmsapp.util

abstract class QueryResult<T> (
    val result: List<T>,
    val count: Int,
)