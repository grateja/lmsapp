package com.vag.lmsapp.app.app_settings.user.add_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumCRUDAction
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityUser
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.util.addOrRemove
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountAddEditViewModel

@Inject
constructor(
    private val repository: UserRepository
): CreateViewModel<EntityUser>(repository) {
//    private val _navigationState = MutableLiveData<NavigationState>()
//    val navigationState: LiveData<NavigationState> = _navigationState

    val confirmPassword = MutableLiveData<String>()
    private var originalName: String? = null
    private var originalEmail: String? = null

    private val _role = MutableLiveData<Role>()
    val role: LiveData<Role> = _role

    private val _permissions = MutableLiveData<MutableList<EnumActionPermission>>()
    val permissions: LiveData<MutableList<EnumActionPermission>> = _permissions

    private val _authId = MutableLiveData<UUID>()
    val authorizedUser = _authId.switchMap { repository.getByIdAsLiveData(it) }

    val canModifyPermissions = MediatorLiveData<Boolean>().apply {
        fun update() {
            val authorized = authorizedUser.value
            val role = role.value

            value = role == Role.STAFF && authorized?.user?.role == Role.OWNER
        }
        addSource(authorizedUser) {update()}
        addSource(_role) {update()}
    }

    fun get(userId: UUID?) {
        viewModelScope.launch {
            super.get(userId, EntityUser()).let {
                originalName = it.name
                originalEmail = it.email
                _role.value = it.role
                _permissions.value = it.permissions.toMutableList()
            }
        }
    }

    fun setRole(role: Role) {
        val authorizer = authorizedUser.value
        if(authorizer?.user?.role == Role.OWNER) {
            _role.value = role
        } else {
            dataState.value = DataState.Invalidate("Only owners can create another owner")
        }
    }

    override fun save() {
        model.value?.role = _role.value ?: Role.STAFF
        model.value?.permissions = _permissions.value ?: listOf(EnumActionPermission.BASIC)
        super.save()
    }

    fun validate() {
        viewModelScope.launch {
            val user = model.value
            val validation = InputValidation().apply {
                addRule("name", user?.name, arrayOf(Rule.Required))
                addRule("email", user?.email, arrayOf(Rule.Required))

                if(originalName != user?.name && repository.checkName(user?.name)) {
                    addError("name", "Name already taken. Please specify more details")
                }

                if(originalEmail != user?.email && repository.checkEmail(user?.email)) {
                    addError("email", "Email already taken")
                }

                if(enumCRUDAction.value == EnumCRUDAction.CREATE) {
                    addRule("password", user?.password, arrayOf(Rule.Required))
                    addRule("confirmPassword", confirmPassword.value, arrayOf(Rule.Required, Rule.Matched(user?.password, "Password do not matched!")))
                }

                if(_permissions.value?.size == 0) {
                    addError("permissions", "Select at least 1 permission!")
                }
            }

            super.isInvalid(validation)
        }
    }

    fun selectPermission(permission: EnumActionPermission) {
        val permissionCache = _permissions.value ?: listOf()
        _permissions.value = permissionCache.toMutableList().apply {
            addOrRemove(permission)
        }
    }

    fun setAuthId(authId: UUID) {
        _authId.value = authId
    }

//    fun clearState() {
//        _navigationState.value = NavigationState.Stateless
//    }

//    sealed class NavigationState {
//        data object Stateless: NavigationState()
//    }
}