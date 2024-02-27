package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumDiscountType
import com.vag.lmsapp.model.EnumDiscountApplicable
//import com.csi.palabakosys.model.DiscountTypeEnum
import com.vag.lmsapp.room.dao.DaoDiscount
import com.vag.lmsapp.room.entities.EntityDiscount


class DiscountsSeeder(dao: DaoDiscount): EntitySeederImpl<EntityDiscount>(dao) {
    override fun items(): List<EntityDiscount> {
        return listOf(
            EntityDiscount("Birthday", 5f,  EnumDiscountType.PERCENTAGE,
                listOf(EnumDiscountApplicable.WASH_DRY_SERVICES)
            ),
            EntityDiscount("PWD", 10f, EnumDiscountType.PERCENTAGE,
                listOf(EnumDiscountApplicable.DELIVERY)
            ),
            EntityDiscount("Senior", 5f, EnumDiscountType.PERCENTAGE,
                listOf(EnumDiscountApplicable.TOTAL_AMOUNT)
            ),
            EntityDiscount("Opening", 10f, EnumDiscountType.FIXED,
                listOf(EnumDiscountApplicable.WASH_DRY_SERVICES, EnumDiscountApplicable.PRODUCTS_CHEMICALS)
            ),
        )
    }
}