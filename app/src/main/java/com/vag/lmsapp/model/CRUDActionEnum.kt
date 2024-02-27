package com.vag.lmsapp.model

enum class CRUDActionEnum(val value: String) {
    CREATE("Create new"),
    RETRIEVE("Retrieve"),
    UPDATE("Update"),
    DELETE("Delete");

    override fun toString(): String {
        return value
    }

    fun toString(suffix: String) : String {
        return value + suffix
    }
}