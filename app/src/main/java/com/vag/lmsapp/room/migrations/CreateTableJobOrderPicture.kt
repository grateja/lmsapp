package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class CreateTableJobOrderPicture : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("""
                CREATE TABLE job_order_pictures (
                    id TEXT PRIMARY KEY NOT NULL,
                    job_order_id TEXT NOT NULL,
                    file_name TEXT NOT NULL,
                    FOREIGN KEY (job_order_id) REFERENCES job_orders(id) ON DELETE CASCADE
                )
            """.trimIndent())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}