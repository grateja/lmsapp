package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.room.dao.DaoService
import com.vag.lmsapp.room.entities.EntityServiceRef
import com.vag.lmsapp.room.entities.EntityService

class WashServicesSeeder(dao: DaoService): EntitySeederImpl<EntityService>(dao) {
    override fun items(): List<EntityService> {
        return listOf(
            EntityService("Hot Wash", 90f, EntityServiceRef(EnumMachineType.REGULAR_WASHER, EnumWashType.HOT, 47)),
            EntityService("Warm Wash", 80f, EntityServiceRef(EnumMachineType.REGULAR_WASHER, EnumWashType.WARM, 36)),
            EntityService("Cold Wash", 70f, EntityServiceRef(EnumMachineType.REGULAR_WASHER, EnumWashType.COLD, 36)),
            EntityService("Delicate Wash", 50f, EntityServiceRef(EnumMachineType.REGULAR_WASHER, EnumWashType.DELICATE, 21)),
            EntityService("Regular Dry", 80f, EntityServiceRef(EnumMachineType.REGULAR_DRYER, null, 40)),
            EntityService("Additional Dry", 30f, EntityServiceRef(EnumMachineType.REGULAR_DRYER, null, 10)),
            EntityService("Hot Wash", 110f, EntityServiceRef(EnumMachineType.TITAN_WASHER, EnumWashType.HOT, 47)),
            EntityService("Warm Wash", 100f, EntityServiceRef(EnumMachineType.TITAN_WASHER, EnumWashType.WARM, 36)),
            EntityService("Cold Wash", 90f, EntityServiceRef(EnumMachineType.TITAN_WASHER, EnumWashType.COLD, 36)),
            EntityService("Delicate Wash", 70f, EntityServiceRef(EnumMachineType.TITAN_WASHER, EnumWashType.DELICATE, 21)),
            EntityService("Regular Dry", 100f, EntityServiceRef(EnumMachineType.TITAN_DRYER, null, 40)),
            EntityService("Additional Dry", 50f, EntityServiceRef(EnumMachineType.TITAN_DRYER, null, 10)),
        )
    }
}