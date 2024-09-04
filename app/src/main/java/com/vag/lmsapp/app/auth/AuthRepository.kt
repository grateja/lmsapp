package com.vag.lmsapp.app.auth

import android.content.Context
import com.vag.lmsapp.room.entities.EntityUser
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.util.Constants.Companion.USER_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository

@Inject
constructor(
    @ApplicationContext context: Context,
    private val userRepository: UserRepository
) {
    private val pref = context.getSharedPreferences("auth_settings", Context.MODE_PRIVATE)

//    fun getLastEmail() : String? {
//        return pref.getString("email", null)
//    }

    private fun getCurrentUserId(): UUID? {
        return pref.getString(USER_ID, null).toUUID()
    }

    suspend fun getCurrentUser() = userRepository.get(getCurrentUserId())
    val currentUserAsLiveData = userRepository.getByIdAsLiveData(getCurrentUserId())

    suspend fun login(email: String?, password: String?) : EntityUser? {
        if(email == null || password == null) return null
        val user = userRepository.getByEmailAndPassword(email, password)
        with(pref.edit()) {
//            this.putString("email", email)
            this.putString(USER_ID, user?.id.toString())
            this.commit()
        }
        return user
    }

    suspend fun login(email: String?, patternIds: ArrayList<Int>) : EntityUser? {
        if(email == null) return null
        val user = userRepository.getByEmailAndPattern(email, patternIds)
        with(pref.edit()) {
//            this.putString("email", email)
            this.putString(USER_ID, user?.id.toString())
            this.commit()
        }
        return user
    }

//    suspend fun login(email: String?, password: String?, remember: Boolean) : EntityUser? {
//        if(email == null || password == null) return null
//        val user = this.oneTimeLogin(email, password)
//        if(remember && user != null) {
//            with(pref.edit()) {
//                this.putString("email", user.email)
//                this.commit()
//            }
//        }
//        return user
//    }
}