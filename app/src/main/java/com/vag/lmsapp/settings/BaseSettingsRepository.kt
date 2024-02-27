package com.vag.lmsapp.settings

open class BaseSettingsRepository(private val dao: SettingsDao) {

    suspend fun getValue(key: String, defaultValue: String) = dao.getValue(key, defaultValue)
    suspend fun getValue(key: String, defaultValue: Int) = dao.getValue(key, defaultValue)
    suspend fun getValue(key: String, defaultValue: Boolean) = dao.getValue(key, defaultValue)
    suspend fun getValue(key: String, defaultValue: Float) = dao.getValue(key, defaultValue)
    suspend fun getValue(key: String, defaultValue: Long) = dao.getValue(key, defaultValue)

    fun getAsLiveData(key: String, defaultValue: String) = dao.getAsLiveData(key, defaultValue)
    fun getAsLiveData(key: String, defaultValue: Boolean) = dao.getAsLiveData(key, defaultValue)
    fun getAsLiveData(key: String, defaultValue: Int) = dao.getAsLiveData(key, defaultValue)
    fun getAsLiveData(key: String, defaultValue: Float) = dao.getAsLiveData(key, defaultValue)
    fun getAsLiveData(key: String, defaultValue: Long) = dao.getAsLiveData(key, defaultValue)

    suspend fun <T> update(value: T, key: String) = dao.update(value, key)
//
//    private suspend fun  <T>writeData(key: Preferences.Key<T>, value: T) = dao.writeData(key, value)
}