package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.room.dao.DaoPackage
import com.vag.lmsapp.room.entities.EntityPackage

class PackageSeeder(private val daoPackage: DaoPackage) : EntitySeederImpl<EntityPackage>(daoPackage) {
    override fun items(): List<EntityPackage> {
        return listOf(
            EntityPackage("Regular Package", "8KG Regular Wash/Dry/Fold", 220f),
            EntityPackage("Titan Package", "12KG Regular Wash/Dry/Fold", 310f),
        )
    }
}