package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.dao.DaoUser
import com.vag.lmsapp.room.entities.EntityUser

class UsersSeeder(private val daoUser: DaoUser) : EntitySeederImpl<EntityUser>(daoUser) {
    override fun items(): List<EntityUser> {
        return listOf(
//            EntityUser(Role.DEVELOPER, "Developer", "developer@dev.com", "123",
//                listOf(EnumActionPermission.ALL), "",
//                arrayListOf(4)
//            ),
            EntityUser(Role.ADMIN, "Admin", "admin@dev.com", "123",
                listOf(
                    EnumActionPermission.ALL,
                ),"",
                arrayListOf(4)
            ),
            EntityUser(Role.STAFF, "Staff 1", "staff1@dev.com", "123",
                listOf(EnumActionPermission.BASIC),"",
                arrayListOf(4)
            ),
            EntityUser(Role.STAFF, "Staff 2", "staff2@dev.com", "123",
                listOf(EnumActionPermission.BASIC),"",
                arrayListOf(4)
            ),
        )
    }
}