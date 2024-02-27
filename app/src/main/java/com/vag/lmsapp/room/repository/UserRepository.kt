package com.vag.lmsapp.room.repository

import androidx.lifecycle.LiveData
import com.vag.lmsapp.room.dao.DaoUser
import com.vag.lmsapp.room.entities.EntityUser
import java.lang.Exception
import java.util.ArrayList
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository
@Inject
constructor (
    private val daoUser: DaoUser,
) : BaseRepository<EntityUser>(daoUser) {
    override suspend fun get(id: UUID?) : EntityUser? {
        if(id == null) return null
        return daoUser.get(id)
    }

    suspend fun getAdmin() : EntityUser? {
        return daoUser.getAdmin()
    }

    suspend fun getByEmail(email: String) : EntityUser? {
        return daoUser.getByEmail(email)
    }

    suspend fun getByEmailAndPassword(email: String, password: String) : EntityUser? {
        return daoUser.getByEmailAndPassword(email, password)
    }

    suspend fun getAll() : List<EntityUser> {
        try {
            return daoUser.getAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun getCurrentUserByEmail(email: String?): LiveData<EntityUser?> {
        return daoUser.getByEmailLiveData(email)
    }

    suspend fun getByEmailAndPattern(email: String, patternIds: ArrayList<Int>): EntityUser? {
        return daoUser.getByEmailAndPattern(email, patternIds)
    }

    fun getAllEmails() = daoUser.getAllEmails()
}