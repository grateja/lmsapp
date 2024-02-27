package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class DropColumnClaimReceiptPicturesUri : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS claim_receipt_pictures_temp (" +
                    "job_order_id TEXT NOT NULL, " +
                    "id TEXT NOT NULL, " +
                    "created_at INTEGER NOT NULL, " +
                    "PRIMARY KEY(id), " +
                    "FOREIGN KEY(job_order_id) REFERENCES job_orders(id) ON DELETE CASCADE)")

            // Copy data from the original table to the temporary table
            database.execSQL("INSERT INTO claim_receipt_pictures_temp SELECT job_order_id, id, created_at FROM claim_receipt_pictures")

            // Drop the original table
            database.execSQL("DROP TABLE IF EXISTS claim_receipt_pictures")

            // Rename the temporary table to the original table name
            database.execSQL("ALTER TABLE claim_receipt_pictures_temp RENAME TO claim_receipt_pictures")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}