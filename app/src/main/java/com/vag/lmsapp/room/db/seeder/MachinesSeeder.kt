package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.dao.DaoMachine
import com.vag.lmsapp.room.entities.EntityMachine

class MachinesSeeder(dao: DaoMachine) : EntitySeederImpl<EntityMachine>(dao) {
    override fun items(): List<EntityMachine> {
        val list = mutableListOf<EntityMachine>()
        var counter = 10
        EnumMachineType.entries.forEach {
            for(i in 1..10) {
                list.add(EntityMachine(i,it, ++counter, i))
            }
        }

        return list
    }
}