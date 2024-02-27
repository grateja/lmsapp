package com.vag.lmsapp.util.converters

import androidx.room.TypeConverter

//object ArrayListConverter {
//    @TypeConverter
//    fun fromArrayList(value: ArrayList<Int>): String {
//        val gson = Gson()
//        return gson.toJson(value)
//    }
//
//    @TypeConverter
//    fun toArrayList(value: String): ArrayList<Int> {
//        val listType = object : TypeToken<ArrayList<Int>>() {}.type
//        return Gson().fromJson(value, listType)
//    }
//}

object ArrayListConverter {

    @TypeConverter
    fun fromArrayList(value: List<Int>): String {
        val stringList = value.joinToString(",") { it.toString() }
        return stringList
    }

    @TypeConverter
    fun toArrayList(value: String): ArrayList<Int> {
        if (value.isEmpty()) {
            return ArrayList()
        }
        val stringList = value.split(",")
        val intList = stringList.map { it.toInt() }
        return ArrayList(intList)
    }
}