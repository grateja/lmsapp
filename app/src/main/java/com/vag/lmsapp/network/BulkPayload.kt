package com.vag.lmsapp.network

import com.vag.lmsapp.room.entities.EntityDeliveryProfile
import com.vag.lmsapp.room.entities.EntityDiscount
import com.vag.lmsapp.room.entities.EntityExtras
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.room.entities.EntityService

data class BulkPayload(
    val services: List<EntityService>,
    val products: List<EntityProduct>,
    val extras: List<EntityExtras>,
    val deliveryProfiles: List<EntityDeliveryProfile>,
    val discounts: List<EntityDiscount>,
)
