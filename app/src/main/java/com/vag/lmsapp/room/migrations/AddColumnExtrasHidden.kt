package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnExtrasHidden : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE extras ADD COLUMN `hidden` INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}