package com.vag.lmsapp.app.auth

import android.content.Intent
import com.vag.lmsapp.model.EnumActionPermission

sealed class AuthResult {
    data class Authenticated(
        val loginCredentials: LoginCredentials,
        val action: String,
        val intent: Intent?
    ): AuthResult()

    data class OperationNotPermitted(
        val permissions: List<EnumActionPermission>,
        val action: String,
        val intent: Intent?,
        val message: String = "You do not have the necessary permission to perform this action",
    ): AuthResult()

    data class MandateAuthentication(
        val permissions: List<EnumActionPermission>,
        val action: String,
        val intent: Intent?
    ): AuthResult()

//    data class NoAuthentication(
//        val action: String,
//        val intent: Intent?,
//    ): AuthResult()
}
