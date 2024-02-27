package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnJobOrderItemsVoid : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE job_order_services ADD COLUMN `void` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_products ADD COLUMN `void` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_extras ADD COLUMN `void` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_delivery_charges ADD COLUMN `void` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_discounts ADD COLUMN `void` INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}