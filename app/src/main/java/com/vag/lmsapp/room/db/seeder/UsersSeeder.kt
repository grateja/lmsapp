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
            EntityUser().apply {
                role = Role.OWNER
                name = "Admin"
                email = "admin@dev.com"
                password = "123"
                permissions = listOf(
                    EnumActionPermission.ALL,
                )
                patternIds = arrayListOf(4)
            },
            EntityUser().apply {
                role = Role.STAFF
                name = "Staff 1"
                email = "staff1@dev.com"
                password = "123"
                permissions = listOf(
                    EnumActionPermission.ALL,
                )
                patternIds = arrayListOf(4)
            },
            EntityUser().apply {
                role = Role.STAFF
                name = "Staff 2"
                email = "staff2@dev.com"
                password = "123"
                permissions = listOf(
                    EnumActionPermission.ALL,
                )
                patternIds = arrayListOf(4)
            },
        )
    }
}