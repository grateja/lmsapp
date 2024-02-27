package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnPackagePrice : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE packages ADD COLUMN total_price REAL")
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}