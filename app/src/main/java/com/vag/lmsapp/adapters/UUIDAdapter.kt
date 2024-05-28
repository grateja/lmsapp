package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.util.UUID

class UUIDAdapter : JsonAdapter<UUID>() {
    @FromJson
    override fun fromJson(reader: JsonReader): UUID? {
        val string = reader.nextString()
        return UUID.fromString(string)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: UUID?) {
        if (value != null) {
            writer.value(value.toString())
        } else {
            writer.nullValue()
        }
    }
}