package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.Instant

class InstantAdapter : JsonAdapter<Instant>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Instant? {
        val string = reader.nextString()
        return Instant.parse(string)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Instant?) {
        if (value != null) {
            writer.value(value.toString())
        } else {
            writer.nullValue()
        }
    }
}