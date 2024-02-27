package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddColumnPaymentVoid : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE job_order_payments ADD COLUMN void_date INTEGER")
            database.execSQL("ALTER TABLE job_order_payments ADD COLUMN void_by TEXT")
            database.execSQL("ALTER TABLE job_order_payments ADD COLUMN void_remarks TEXT")
        }catch(e: Exception){
            e.printStackTrace()
        }
    }
}