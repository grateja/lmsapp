package com.vag.lmsapp.network.requests_body

import com.vag.lmsapp.room.entities.EntityDeliveryProfile
import com.vag.lmsapp.room.entities.EntityDiscount
import com.vag.lmsapp.room.entities.EntityExtras
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.room.entities.EntityService
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.entities.EntityUser

data class SetupRequestBody(
    val shop: EntityShop,
    val staffs: List<EntityUser>,
    val services: List<EntityService>,
    val products: List<EntityProduct>,
    val extras: List<EntityExtras>,
    val deliveryProfiles: List<EntityDeliveryProfile>,
    val discounts: List<EntityDiscount>
)
