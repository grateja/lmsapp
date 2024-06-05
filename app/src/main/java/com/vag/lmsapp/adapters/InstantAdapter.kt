package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import com.vag.lmsapp.util.toShort
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InstantAdapter : JsonAdapter<Instant>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Instant? {
        val string = reader.nextString()
        return Instant.parse(string)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Instant?) {
        if (value != null) {
            val utcDateTime = value.atZone(ZoneId.of("UTC"))

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDate = utcDateTime.format(formatter)

            writer.value(formattedDate)
        } else {
            writer.nullValue()
        }
    }
}