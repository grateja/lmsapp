package com.vag.lmsapp.settings

import androidx.lifecycle.switchMap
import com.vag.lmsapp.app.auth.LoginCredentials
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.room.entities.EntityUser
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.toUUID
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecuritySettingsRepository

@Inject
constructor(
    private val dao: SettingsDao,
    private val userRepository: UserRepository
) : BaseSettingsRepository(dao) {
    companion object {
        const val SECURITY_TYPE = "security_type"
    }

    private val _currentUserId = getAsLiveData(USER_ID, "")
    val currentUser = _currentUserId.switchMap {
        userRepository.getByIdAsLiveData(it.toUUID())
    }

    suspend fun getCurrentUser(): EntityUser? {
        return userRepository.get(getValue(USER_ID, "").toUUID())
    }

    suspend fun setCurrentUser(userId: UUID?) {
        update(userId.toString(), USER_ID)
    }

    val securityTypeOrdinal = getAsLiveData(SECURITY_TYPE, EnumSecurityType.SENSITIVE_ACTIONS.ordinal)

    suspend fun getSecurityType(): EnumSecurityType {
        val ordinal = getValue(SECURITY_TYPE, EnumSecurityType.SENSITIVE_ACTIONS.ordinal)
        return EnumSecurityType.entries[ordinal]
    }

    suspend fun updateSecurityType(securityType: EnumSecurityType) {
        update(securityType.ordinal, SECURITY_TYPE)
    }

    suspend fun verifyPermissions(requiredPermissions: List<EnumActionPermission>?): LoginCredentials? {
        val user = getCurrentUser()
        val userPermissions = user?.permissions
        if(EnumActionPermission.deniedPermissions(userPermissions, requiredPermissions).isNotEmpty()) {
            return null
        } else if(user != null) {
            return LoginCredentials(
                user.email, user.password, user.id, user.name, user.permissions, user.role
            )
        } else {
            return null
        }
    }
}