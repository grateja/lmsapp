package com.vag.lmsapp.app.app_settings.user

import androidx.room.Embedded
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.room.entities.EntityUser

data class UserPreview(
    @Embedded
    val user: EntityUser
) {
//    fun permissionStr() : String {
//        if(user.permissions.size == 1) {
//            return user.permissions.first().toString()
//        }
//        return "${user.permissions.size} permissions"
//    }

    fun label(): String {
        return "${user.name} [${user.role}], ${user.permissionStr()}"
    }
}