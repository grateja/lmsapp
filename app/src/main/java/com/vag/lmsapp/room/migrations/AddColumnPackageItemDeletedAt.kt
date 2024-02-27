package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnPackageItemDeletedAt : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE package_services ADD COLUMN deleted_at INTEGER")
            database.execSQL("ALTER TABLE package_products ADD COLUMN deleted_at INTEGER")
            database.execSQL("ALTER TABLE package_extras ADD COLUMN deleted_at INTEGER")
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}