package com.vag.lmsapp.app.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class LoginCredentials(
    val email: String?,
    val password: String?,
    val userId: UUID,
    val userName: String?
) : Parcelable