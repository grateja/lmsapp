package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class CreateTablePackage : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("""
                CREATE TABLE `packages` (
                    `package_name` TEXT NOT NULL,
                    `description` TEXT,
                    `delivery_id` TEXT,
                    `discount_id` TEXT,
                    `fixed_price` REAL,
                    `id` TEXT NOT NULL,
                    `created_at` INTEGER NOT NULL,
                    `updated_at` INTEGER NOT NULL,
                    `deleted_at` INTEGER,
                    `sync` INTEGER,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}