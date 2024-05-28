package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.internal.NullSafeJsonAdapter
import java.lang.reflect.Type

class NullSafeAdapter<T>(private val delegate: JsonAdapter<T>) : JsonAdapter<T>() {
    @ToJson
    override fun toJson(writer: JsonWriter, value: T?) {
        writer.serializeNulls = true
        delegate.toJson(writer, value)
    }

    @FromJson
    override fun fromJson(reader: com.squareup.moshi.JsonReader): T? {
        return delegate.fromJson(reader)
    }

    companion object {
        fun <T> create(type: Type, moshi: Moshi): JsonAdapter<T> {
            val delegate = moshi.nextAdapter<T>(null, type, emptySet())
            return NullSafeJsonAdapter(delegate)
        }
    }
}