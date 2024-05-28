package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson


class ArrayListIntAdapter : JsonAdapter<ArrayList<Int>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): ArrayList<Int>? {
        val string = reader.nextString()
        return if (string.isEmpty()) {
            ArrayList()
        } else {
            string.split(",").map { it.toInt() }.toCollection(ArrayList())
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ArrayList<Int>?) {
        if (value != null) {
            // Join the integers into a comma-separated string
            val string = value.joinToString(",")
            writer.value(string)
        } else {
            writer.nullValue()
        }
    }
}