package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumDiscountApplicable
import com.vag.lmsapp.room.dao.DaoDiscount
import com.vag.lmsapp.room.entities.EntityDiscount


class DiscountsSeeder(dao: DaoDiscount): EntitySeederImpl<EntityDiscount>(dao) {
    override fun items(): List<EntityDiscount> {
        return listOf(
            EntityDiscount("Birthday", 5f,
                listOf(EnumDiscountApplicable.WASH_DRY_SERVICES)
            ),
            EntityDiscount("PWD", 10f,
                listOf(EnumDiscountApplicable.DELIVERY)
            ),
            EntityDiscount("Senior", 5f,
                listOf(EnumDiscountApplicable.TOTAL_AMOUNT)
            ),
            EntityDiscount("Opening", 10f,
                listOf(EnumDiscountApplicable.WASH_DRY_SERVICES, EnumDiscountApplicable.PRODUCTS_CHEMICALS)
            ),
        )
    }
}