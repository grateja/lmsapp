package com.vag.lmsapp.model

enum class EnumCRUDAction(val id: Int, val value: String) {
    CREATE(1, "Create new"),
    UPDATE(2, "Update"),
    DELETE(3, "Delete");

    override fun toString(): String {
        return value
    }

    fun toString(suffix: String) : String {
        return value + suffix
    }

    companion object {
        fun fromId(id: Int) : EnumCRUDAction? {
            return entries.find { it.id == id }
        }
    }
}