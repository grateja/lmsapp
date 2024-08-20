package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnMachineUserId : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE machines ADD COLUMN `user_id` TEXT NULL")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}