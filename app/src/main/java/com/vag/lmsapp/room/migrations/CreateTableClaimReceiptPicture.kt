package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class CreateTableClaimReceiptPicture : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("""
                CREATE TABLE claim_receipt_pictures (
                    id TEXT PRIMARY KEY NOT NULL,
                    job_order_id TEXT NOT NULL,
                    uri_id INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY (job_order_id) REFERENCES job_orders(id) ON DELETE CASCADE
                )
            """.trimIndent())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}