package com.vag.lmsapp.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.vag.lmsapp.app.auth.LoginCredentials
import kotlin.reflect.KClass

sealed class DataState<out R> {
    object StateLess : DataState<Nothing>()
    data class SaveSuccess<out T> (val data: T) : DataState<T>()
    data class DeleteSuccess<out T>(val data: T) : DataState<T>()
    data class Invalidate(val message: String) : DataState<Nothing>()
    data class InvalidInput<out T>(val inputValidation: InputValidation) : DataState<T>()
    object ValidationPassed : DataState<Nothing>()
    data class AuthenticationPassed<out T>(val loginCredentials: LoginCredentials) : DataState<T>()
    data class RequestExit(val promptPass: Boolean) : DataState<Nothing>()
    data class Submit<out T>(val data: T): DataState<T>()
    data class LoadItems<R>(val items: List<R>, val reset: Boolean) : DataState<R>()
    data class ShowDateRangePicker(val dateFilter: DateFilter?): DataState<Nothing>()
    data class OpenActivity(val activityClass: Class<*>?, val extras: Bundle? = null, val launchCode: Int? = null): DataState<Nothing>()
}