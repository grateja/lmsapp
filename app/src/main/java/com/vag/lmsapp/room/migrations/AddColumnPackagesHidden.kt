package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnPackagesHidden : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE packages ADD COLUMN `hidden` INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}