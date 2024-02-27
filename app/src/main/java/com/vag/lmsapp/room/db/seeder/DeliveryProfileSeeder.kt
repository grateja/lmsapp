package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumDeliveryVehicle
import com.vag.lmsapp.room.dao.DaoDeliveryProfile
import com.vag.lmsapp.room.entities.EntityDeliveryProfile


class DeliveryProfileSeeder(dao: DaoDeliveryProfile): EntitySeederImpl<EntityDeliveryProfile>(dao) {
    override fun items(): List<EntityDeliveryProfile> {
        return listOf(
            EntityDeliveryProfile(EnumDeliveryVehicle.TRIKE_PEDAL, 20f, 10f, 1f),
            EntityDeliveryProfile(EnumDeliveryVehicle.TRIKE_ELECTRIC, 25f, 10f, 5f),
            EntityDeliveryProfile(EnumDeliveryVehicle.MOTORCYCLE, 30f, 12f, 10f),
            EntityDeliveryProfile(EnumDeliveryVehicle.TRICYCLE, 40f, 12f, 5f),
            EntityDeliveryProfile(EnumDeliveryVehicle.SEDAN, 50f, 15f, 30f),
            EntityDeliveryProfile(EnumDeliveryVehicle.MPV, 60f, 15f, 50f),
            EntityDeliveryProfile(EnumDeliveryVehicle.SMALL_VAN, 70f, 17f, 100f),
        )
    }
}