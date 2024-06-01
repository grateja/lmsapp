package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.app_settings.user.UserPreview
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.entities.EntityUser
import java.util.ArrayList
import java.util.UUID

@Dao
abstract class DaoUser : BaseDao<EntityUser> {
    @Query("SELECT * FROM users WHERE id = :id")
    abstract suspend fun get(id: UUID) : EntityUser?

    @Query("SELECT * FROM users WHERE email = :email")
    abstract suspend fun getByEmail(email: String) : EntityUser?

    @Query("SELECT * FROM users")
    abstract suspend fun getAll() : List<EntityUser>

    @Query("SELECT * FROM users WHERE :role IS NULL OR role = :role")
    abstract fun getByRoleAsLiveData(role: Role?) : LiveData<List<UserPreview>>

    @Query("SELECT * FROM users WHERE role = 'admin'")
    abstract suspend fun getAdmin() : EntityUser?

    @Query("SELECT * FROM users WHERE email = :email AND deleted = 0 LIMIT 1")
    abstract fun getByEmailLiveData(email: String?): LiveData<EntityUser?>

    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND deleted = 0 LIMIT 1")
    abstract suspend fun getByEmailAndPassword(email: String, password: String): EntityUser?

    @Query("SELECT * FROM users WHERE email = :email AND pattern_ids = :patternIds AND deleted = 0 LIMIT 1")
    abstract suspend fun getByEmailAndPattern(email: String, patternIds: ArrayList<Int>): EntityUser?

    @Query("SELECT email FROM users WHERE deleted = 0")
    abstract fun getAllEmails(): LiveData<List<String>>

    @Query("SELECT * FROM users WHERE id = :id")
    abstract fun getByIdAsLiveData(id: UUID?): LiveData<UserPreview>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE name LIKE :name AND deleted = 0)")
    abstract suspend fun checkName(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email LIKE :email AND deleted = 0)")
    abstract suspend fun checkEmail(email: String?): Boolean

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    abstract suspend fun changePassword(userId: UUID, newPassword: String)

    @Query("SELECT * FROM users WHERE sync = 0")
    abstract suspend fun unSynced(): List<EntityUser>
}