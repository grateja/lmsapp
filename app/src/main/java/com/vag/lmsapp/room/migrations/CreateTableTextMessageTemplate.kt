package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class CreateTableTextMessageTemplate: Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("""
                CREATE TABLE text_message_templates (
                    `id` TEXT PRIMARY KEY NOT NULL,
                    `title` TEXT NOT NULL,
                    `message` TEXT NOT NULL,
                    `created_at` INTEGER NOT NULL,
                    `updated_at` INTEGER NOT NULL,
                    `deleted` INTEGER NOT NULL DEFAULT 0,
                    `sync` INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}