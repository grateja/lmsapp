package com.vag.lmsapp.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AddForeignKeysPackageId : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("CREATE INDEX index_package_id ON package_services (package_id)")
            database.execSQL("CREATE TABLE `temp` (`package_id` TEXT NOT NULL, `service_id` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `id` TEXT NOT NULL, deleted_at INTEGER, PRIMARY KEY(`id`), FOREIGN KEY (`package_id`) REFERENCES `packages`(`id`) ON DELETE CASCADE)")
            database.execSQL("INSERT INTO `temp` (`id`, `package_id`, `service_id`, `quantity`, `deleted_at`) SELECT `id`, `package_id`, `service_id`, `quantity`, `deleted_at` FROM `package_services`")
            database.execSQL("DROP TABLE `package_services`")
            database.execSQL("ALTER TABLE `temp` RENAME TO `package_services`")

            database.execSQL("CREATE INDEX index_package_id ON package_products (package_id)")
            database.execSQL("CREATE TABLE `temp` (`package_id` TEXT NOT NULL, `product_id` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `id` TEXT NOT NULL, deleted_at INTEGER, PRIMARY KEY(`id`), FOREIGN KEY (`package_id`) REFERENCES `packages`(`id`) ON DELETE CASCADE)")
            database.execSQL("INSERT INTO `temp` (`id`, `package_id`, `product_id`, `quantity`, `deleted_at`) SELECT `id`, `package_id`, `product_id`, `quantity`, `deleted_at` FROM `package_products`")
            database.execSQL("DROP TABLE `package_products`")
            database.execSQL("ALTER TABLE `temp` RENAME TO `package_products`")

            database.execSQL("CREATE INDEX index_package_id ON package_extras (package_id)")
            database.execSQL("CREATE TABLE `temp` (`package_id` TEXT NOT NULL, `extras_id` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `id` TEXT NOT NULL, deleted_at INTEGER, PRIMARY KEY(`id`), FOREIGN KEY (`package_id`) REFERENCES `packages`(`id`) ON DELETE CASCADE)")
            database.execSQL("INSERT INTO `temp` (`id`, `package_id`, `extras_id`, `quantity`, `deleted_at`) SELECT `id`, `package_id`, `extras_id`, `quantity`, `deleted_at` FROM `package_extras`")
            database.execSQL("DROP TABLE `package_extras`")
            database.execSQL("ALTER TABLE `temp` RENAME TO `package_extras`")

        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}