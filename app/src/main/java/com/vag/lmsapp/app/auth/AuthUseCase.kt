package com.vag.lmsapp.app.auth

import android.content.Intent
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.settings.SecuritySettingsRepository
import javax.inject.Inject

class AuthUseCase

@Inject
constructor(private val securitySettingsRepository: SecuritySettingsRepository) {
    suspend operator fun invoke(requiredPermissions: List<EnumActionPermission>, authAction: String, mandate: Boolean, intent: Intent? = null): AuthResult {
        val securityType = securitySettingsRepository.getSecurityType()

        val user = securitySettingsRepository.getCurrentUser()

        if(mandate || user == null || securityType == EnumSecurityType.SENSITIVE_ACTIONS)
            return AuthResult.MandateAuthentication(requiredPermissions, authAction, intent)

        if(requiredPermissions.isNotEmpty()) {
            val deniedPermissions = EnumActionPermission.deniedPermissions(user.permissions, requiredPermissions)
            if(deniedPermissions.isNotEmpty()) {
                return AuthResult.OperationNotPermitted(requiredPermissions, authAction, intent)
            }
        }

        return AuthResult.Authenticated(
            LoginCredentials(
                user.email,
                user.password,
                user.id,
                user.name,
                user.role
            ),
            authAction,
            intent
        )
    }
}