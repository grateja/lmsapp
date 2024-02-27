package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.room.dao.DaoProduct
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.model.EnumMeasureUnit


class ProductsSeeder(dao: DaoProduct): EntitySeederImpl<EntityProduct>(dao) {
    override fun items(): List<EntityProduct> {
        return listOf(
            EntityProduct("Ariel",15f, 100, EnumMeasureUnit.SACHET, 1f, EnumProductType.DETERGENT),
            EntityProduct("Downy",15f, 100, EnumMeasureUnit.SACHET, 1f, EnumProductType.FAB_CON),
            EntityProduct("Hanger",35f, 100, EnumMeasureUnit.PCS, 1f, EnumProductType.OTHER),
            EntityProduct("Plastic",5f, 100, EnumMeasureUnit.PCS, 1f, EnumProductType.DETERGENT),
        )
    }
}