package com.vag.lmsapp.app.app_settings.user

import com.vag.lmsapp.model.Role

data class CurrentUser(
    val id: String,
    val name: String,
    val email: String,
    val role: Role
)