package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnMachineUsageUserId : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE machine_usages ADD COLUMN `user_id` TEXT NULL")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}