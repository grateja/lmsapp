package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnJobOrderRemarks : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE job_orders ADD COLUMN `remarks` TEXT NULL")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}