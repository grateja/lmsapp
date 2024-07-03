package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.room.dao.DaoService
import com.vag.lmsapp.room.entities.EntityServiceRef
import com.vag.lmsapp.room.entities.EntityService

class WashServicesSeeder(dao: DaoService): EntitySeederImpl<EntityService>(dao) {
    override fun items(): List<EntityService> {
        return listOf(
            EntityService("Super Wash", 100f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.REGULAR, EnumWashType.SUPER_WASH, 47)),
            EntityService("Hot Wash", 90f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.REGULAR, EnumWashType.HOT, 44)),
            EntityService("Warm Wash", 80f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.REGULAR, EnumWashType.WARM, 36)),
            EntityService("Cold Wash", 70f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.REGULAR, EnumWashType.COLD, 36)),
            EntityService("Delicate Wash", 50f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.REGULAR, EnumWashType.DELICATE, 21)),
            EntityService("Regular Dry", 80f, EntityServiceRef(EnumServiceType.DRY, EnumMachineType.REGULAR, null, 40)),
            EntityService("Additional Dry", 30f, EntityServiceRef(EnumServiceType.DRY, EnumMachineType.REGULAR, null, 10)),
            EntityService("Super Wash", 130f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.TITAN, EnumWashType.SUPER_WASH, 47)),
            EntityService("Hot Wash", 110f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.TITAN, EnumWashType.HOT, 44)),
            EntityService("Warm Wash", 100f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.TITAN, EnumWashType.WARM, 36)),
            EntityService("Cold Wash", 90f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.TITAN, EnumWashType.COLD, 36)),
            EntityService("Delicate Wash", 70f, EntityServiceRef(EnumServiceType.WASH, EnumMachineType.TITAN, EnumWashType.DELICATE, 21)),
            EntityService("Regular Dry", 100f, EntityServiceRef(EnumServiceType.DRY, EnumMachineType.TITAN, null, 40)),
            EntityService("Additional Dry", 50f, EntityServiceRef(EnumServiceType.DRY, EnumMachineType.TITAN, null, 10)),
        )
    }
}