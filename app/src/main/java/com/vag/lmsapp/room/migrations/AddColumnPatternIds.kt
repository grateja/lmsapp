package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnPatternIds : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE users ADD COLUMN pattern_ids TEXT NOT NULL DEFAULT '[]'")
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}