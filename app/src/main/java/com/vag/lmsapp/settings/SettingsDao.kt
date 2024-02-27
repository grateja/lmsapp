package com.vag.lmsapp.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDao

@Inject
constructor(@ApplicationContext private val context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun getValue(key: String, defaultValue: String) : String {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(key)] ?: defaultValue
            }
            .first()
    }

    suspend fun getValue(key: String, defaultValue: Int) : Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey(key)] ?: defaultValue
            }
            .first()
    }

    suspend fun getValue(key: String, defaultValue: Boolean) : Boolean {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(key)] ?: defaultValue
            }
            .first()
    }

    suspend fun getValue(key: String, defaultValue: Float) : Float {
        return context.dataStore.data
            .map { preferences ->
                preferences[floatPreferencesKey(key)] ?: defaultValue
            }
            .first()
    }

    suspend fun getValue(key: String, defaultValue: Long) : Long {
        return context.dataStore.data
            .map { preferences ->
                preferences[longPreferencesKey(key)] ?: defaultValue
            }
            .first()
    }

    fun getAsLiveData(key: String, defaultValue: String) : LiveData<String> {
        val prefKey = stringPreferencesKey(key)

        setDefaultValueIfNotExists(prefKey, defaultValue)

        val flow = context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
        return flow.asLiveData()
    }

    fun getAsLiveData(key: String, defaultValue: Boolean) : LiveData<Boolean> {
        val prefKey = booleanPreferencesKey(key)

        setDefaultValueIfNotExists(prefKey, defaultValue)

        val flow = context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
        return flow.asLiveData()
    }

    fun getAsLiveData(key: String, defaultValue: Int) : LiveData<Int> {
        val prefKey = intPreferencesKey(key)

        setDefaultValueIfNotExists(prefKey, defaultValue)

        val flow = context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
        return flow.asLiveData()
    }

    fun getAsLiveData(key: String, defaultValue: Float) : LiveData<Float> {
        val prefKey = floatPreferencesKey(key)

        setDefaultValueIfNotExists(prefKey, defaultValue)

        val flow = context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
        return flow.asLiveData()
    }

    fun getAsLiveData(key: String, defaultValue: Long) : LiveData<Long> {
        return getAsLiveData(
            longPreferencesKey(key),
            defaultValue
        )
    }

    private inline fun <reified T>getAsLiveData(key: Preferences.Key<T>, defaultValue: T): LiveData<T> {
        setDefaultValueIfNotExists(key, defaultValue)
        val flow = context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
        return flow.asLiveData()
    }

    private fun <T>setDefaultValueIfNotExists(key: Preferences.Key<T>, defaultValue: T) {
        runBlocking {
            context.dataStore.edit { preferences ->
                if (!preferences.contains(key)) {
                    preferences[key] = defaultValue
                }
            }
        }
    }

    suspend fun <T> update(value: T, key: String) {
        if(value != null) {
            when (value!!::class) {
                Int::class -> writeData(intPreferencesKey(key), value as Int)
                Long::class -> writeData(longPreferencesKey(key), value as Long)
                Float::class -> writeData(floatPreferencesKey(key), value as Float)
                Double::class -> writeData(doublePreferencesKey(key), value as Double)
                Boolean::class -> writeData(booleanPreferencesKey(key), value as Boolean)
                String::class -> writeData(stringPreferencesKey(key), value as String)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    private suspend fun  <T>writeData(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}