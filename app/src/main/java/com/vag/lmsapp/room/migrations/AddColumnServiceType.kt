package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnServiceType : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE services ADD COLUMN `svc_service_type` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE job_order_services ADD COLUMN `svc_service_type` INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}