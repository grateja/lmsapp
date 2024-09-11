package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.app_settings.user.UserPreview
import com.vag.lmsapp.app.app_settings.user.list.advanced_filter.UserAccountAdvancedFilter
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.entities.EntityUser
import com.vag.lmsapp.util.QueryResult
import com.vag.lmsapp.util.ResultCount
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

    @Query("""
        SELECT *
        FROM users
        WHERE (name LIKE '%' || :keyword || '%')
            AND (role = :role OR :role IS NULL)
            AND deleted = 0
        ORDER BY
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'ASC' THEN name END ASC,
            CASE WHEN :orderBy = 'Email' AND :sortDirection = 'ASC' THEN email END ASC,
            CASE WHEN :orderBy = 'Name' AND :sortDirection = 'DESC' THEN name END DESC,
            CASE WHEN :orderBy = 'Email' AND :sortDirection = 'DESC' THEN email END DESC
        LIMIT 20 OFFSET :offset
    """)
    abstract suspend fun filter(keyword: String, role: Role?, offset: Int, orderBy: String, sortDirection: String): List<UserPreview>

    @Query("""SELECT(
        SELECT COUNT(*)
        FROM users
        WHERE (name LIKE '%' || :keyword || '%')
            AND (role = :role OR :role IS NULL)
            AND deleted = 0
    ) AS filtered, (
        SELECT COUNT(*)
        FROM users
        WHERE deleted = 0
    ) AS total
    """)
    abstract suspend fun count(keyword: String, role: Role?): ResultCount

    suspend fun queryResult(keyword: String, offset: Int, advancedFilter: UserAccountAdvancedFilter): QueryResult<UserPreview> {
        return QueryResult(
            filter(keyword, advancedFilter.role, offset, advancedFilter.orderBy, advancedFilter.sortDirection.toString()),
            count(keyword, advancedFilter.role)
        )
    }

    @Query("SELECT * FROM users WHERE :role IS NULL OR role = :role")
    abstract fun getByRoleAsLiveData(role: Role?) : LiveData<List<UserPreview>>

    @Query("SELECT * FROM users WHERE role = 'owner'")
    abstract suspend fun getOwners() : List<EntityUser>

    @Query("SELECT * FROM users WHERE email = :email AND deleted = 0 LIMIT 1")
    abstract fun getByEmailLiveAsData(email: String?): LiveData<EntityUser?>

    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND deleted = 0 LIMIT 1")
    abstract suspend fun getByEmailAndPassword(email: String?, password: String?): EntityUser?

    @Query("SELECT * FROM users WHERE email = :email AND pattern_ids = :patternIds AND deleted = 0 LIMIT 1")
    abstract suspend fun getByEmailAndPattern(email: String?, patternIds: ArrayList<Int>): EntityUser?

    @Query("SELECT email FROM users WHERE deleted = 0")
    abstract fun getAllEmails(): LiveData<List<String>>

    @Query("SELECT * FROM users WHERE id = :id")
    abstract fun getByIdAsLiveData(id: UUID?): LiveData<UserPreview?>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE name LIKE :name AND deleted = 0)")
    abstract suspend fun checkName(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email LIKE :email AND deleted = 0)")
    abstract suspend fun checkEmail(email: String?): Boolean

    @Query("UPDATE users SET password = :newPassword, pattern_ids = '' WHERE id = :userId")
    abstract suspend fun changePassword(userId: UUID, newPassword: String)

    @Query("SELECT * FROM users WHERE sync = 0 OR :forced")
    abstract suspend fun unSynced(forced: Boolean): List<EntityUser>

    @Query("UPDATE users SET pattern_ids = :patternIds, password = '' WHERE id = :userId")
    abstract suspend fun changePattern(userId: UUID, patternIds: ArrayList<Int>)
}