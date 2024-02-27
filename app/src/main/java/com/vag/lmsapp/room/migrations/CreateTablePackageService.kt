package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class CreateTablePackageService : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("""
                CREATE TABLE `package_services` (
                    `package_id` TEXT NOT NULL,
                    `service_id` TEXT NOT NULL,
                    `quantity` INTEGER NOT NULL,
                    `unit_price` REAL NOT NULL,
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