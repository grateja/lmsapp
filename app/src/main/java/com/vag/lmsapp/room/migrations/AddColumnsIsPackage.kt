package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnsIsPackage : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE job_order_services ADD COLUMN `is_package` NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_products ADD COLUMN `is_package` NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_extras ADD COLUMN `is_package` NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_discounts ADD COLUMN `is_package` NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_delivery_charges ADD COLUMN `is_package` NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}