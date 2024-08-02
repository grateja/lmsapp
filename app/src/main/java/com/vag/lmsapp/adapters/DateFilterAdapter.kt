package com.vag.lmsapp.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.LocalDate

class DateFilterAdapter: JsonAdapter<LocalDate>() {
    @FromJson
    override fun fromJson(reader: JsonReader): LocalDate {
        return LocalDate.parse(reader.nextString())
    }

    @ToJson
    override fun toJson(writer: JsonWriter, localDate: LocalDate?) {
        writer.value(localDate?.toString())
    }
}

//class DateFilterAdapter: JsonAdapter<DateFilter>() {
//    @FromJson
//    override fun fromJson(reader: JsonReader): DateFilter {
//        var dateFrom: LocalDate? = null
//        var dateTo: LocalDate? = null
//        reader.beginObject()
//        while (reader.hasNext()) {
//            val name = reader.nextName()
//            when (name) {
//                "dateFrom" -> dateFrom = LocalDate.parse(reader.nextString())
//                "dateTo" -> dateTo = if (reader.peek() == JsonReader.Token.NULL) null else LocalDate.parse(reader.nextString())
//                else -> reader.skipValue()
//            }
//        }
//        reader.endObject()
//        return DateFilter(dateFrom ?: LocalDate.now(), dateTo)
//    }
//
//    @ToJson
//    override fun toJson(writer: JsonWriter, dateFilter: DateFilter?) {
//        writer.beginObject()
//        writer.name("dateFrom")
//        writer.value(dateFilter?.dateFrom?.toString())
//        writer.name("dateTo")
//        writer.value(dateFilter?.dateTo?.toString())
//        writer.endObject()
//    }
//}