package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnJobOrderItemDiscountedPrice : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE job_order_services ADD COLUMN `discounted_price` REAL NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_products ADD COLUMN `discounted_price` REAL NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_extras ADD COLUMN `discounted_price` REAL NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE job_order_delivery_charges ADD COLUMN `discounted_price` REAL NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}