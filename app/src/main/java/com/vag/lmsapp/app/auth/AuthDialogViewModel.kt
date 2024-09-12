package com.vag.lmsapp.app.auth

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumActionPermission
import com.vag.lmsapp.model.EnumAuthMethod
import com.vag.lmsapp.model.EnumSecurityType
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityUser
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.settings.SecuritySettingsRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class AuthDialogViewModel

@Inject
constructor(
    private val userRepository: UserRepository,
    private val securitySettingsRepository: SecuritySettingsRepository
): ViewModel() {
    private val _action = MutableLiveData<String>()
    val action: LiveData<String> = _action

    private val _securityTypeOrdinal = securitySettingsRepository.securityTypeOrdinal
    val securityType = MediatorLiveData<EnumSecurityType>().apply {
        addSource(_securityTypeOrdinal) {
            value = EnumSecurityType.entries[it]
        }
    }

    val title = MediatorLiveData<String>().apply {
        addSource(securityType) {
            value = if(it == EnumSecurityType.START_UP) {
                "Start up Authentication required"
            } else if(it == EnumSecurityType.SENSITIVE_ACTIONS) {
                "This action requires authentication"
            } else {
                "Authentication required"
            }
        }
    }

//    private val _authMethod = MutableLiveData(EnumAuthMethod.AUTH_BY_PATTERN)
//    val authMethod: LiveData<EnumAuthMethod> = _authMethod
//
    private val _dataState = MutableLiveData<DataState<LoginCredentials>>()
    val dataState: LiveData<DataState<LoginCredentials>> = _dataState

    private val _inputValidation = MutableLiveData(InputValidation())
    val validation: LiveData<InputValidation> = _inputValidation

    val userName = MutableLiveData("")
    val password = MutableLiveData("")

    val user = userName.switchMap { userRepository.getByEmailAsLiveData(it) }

    private val _permissions = MutableLiveData<List<EnumActionPermission>>()
    val permissions: LiveData<List<EnumActionPermission>> = _permissions

    private val _roles = MutableLiveData<List<Role>>()
    val roles: LiveData<List<Role>> = _roles

    val emails = userRepository.getAllEmails()

    val authMethod = MediatorLiveData<EnumAuthMethod?>().apply {
        addSource(user) {
            val pattern = it?.patternIds
            val password = it?.password
            value = if(pattern != null && pattern.size > 0) {
                EnumAuthMethod.AUTH_BY_PATTERN
            } else if(password != null) {
                EnumAuthMethod.AUTH_BY_PASSWORD
            } else {
                null
            }
        }
    }

    fun setLaunchCode(launchCode: String) {
        _action.value = launchCode
    }

//    fun setAuthMethod(authMethod: EnumAuthMethod) {
//        _authMethod.value = authMethod
//    }

    fun clearError(key: String) {
        _inputValidation.value = _inputValidation.value?.removeError(key)
    }

    fun setPermissions(permissions: List<EnumActionPermission>) {
        _permissions.value = permissions
    }

    fun clearState() {
        _dataState.value = DataState.StateLess
    }

    @SuppressLint("Get all the permissions not present on user privileges")
    private fun checkPermissions(userPrivileges: List<EnumActionPermission>) : List<EnumActionPermission> {
        if(userPrivileges.contains(EnumActionPermission.ALL)) return emptyList()

        val result = mutableListOf<EnumActionPermission>()

        _permissions.value?.let { permissions ->
            for (item in permissions) {
                if (!userPrivileges.contains(item)) {
                    result.add(item)
                }
            }
        }
        return result
    }

    fun validate(method: AuthMethod) {
        viewModelScope.launch {
            InputValidation().apply {
                val password = password.value
                val email = userName.value
                val roles = _roles.value

                this.addRule("userName", email, arrayOf(Rule.Required))
                if(method is AuthMethod.AuthByPassword) {
                    this.addRule("password", password, arrayOf(Rule.Required))
                }

                if(this.isInvalid()) {
                    _dataState.value = DataState.InvalidInput(this)
                } else {
                    val user = when(method) {
                        is AuthMethod.AuthByPassword -> {
                            userRepository.getByEmailAndPassword(email, password)
                        }
                        is AuthMethod.AuthByPattern -> {
                            userRepository.getByEmailAndPattern(email, method.pattern)
                        }
                    }

                    user.let {
                        if(it != null) {
                            if(checkPermissions(it)) {
                                securitySettingsRepository.setCurrentUser(it.id)
                                println("set user")
                                println(it)
                            }
////                            val deniedPermissions = checkPermissions(it.permissions)
//                            val permissions = _permissions.value ?: emptyList()
//                            val deniedPermissions = EnumActionPermission.deniedPermissions(it.permissions, permissions)
//                            val hasRole = roles?.contains(it.role)
//
//                            if(deniedPermissions.isNotEmpty() || (hasRole != null && hasRole == false)) {
//                                _dataState.value = DataState.Invalidate("You do not have the necessary permissions to perform this action.")
//                            } else {
//                                securitySettingsRepository.setCurrentUser(it.id)
//                                _dataState.value = DataState.SaveSuccess(
//                                    LoginCredentials(it.email, it.password, it.id, it.name, it.role)
//                                )
//                            }
                        } else {
                            _dataState.value = DataState.Invalidate("Invalid login credentials")
                        }
                    }
                }

                _inputValidation.value = this
            }
        }
    }

    fun setRoles(roles: ArrayList<Role>) {
        _roles.value = roles
    }

//    fun checkSecurityType(mandate: Boolean) {
//        if(mandate) return // Security type is disregarded. Authentication is required
//
//        viewModelScope.launch {
//            securitySettingsRepository.getSecurityType().let {
//                if(it == EnumSecurityType.START_UP) {
//                    securitySettingsRepository.getCurrentUser()?.let {
//                        checkPermissions(it)
//                    }
//                }
//            }
//        }
//    }

    private fun checkPermissions(user: EntityUser): Boolean {
        val hasRole = _roles.value?.contains(user.role)
        return if(EnumActionPermission.deniedPermissions(
            user.permissions,
            _permissions.value
        ).isNotEmpty() || (hasRole != null && hasRole == false)) {
            _dataState.value = DataState.Invalidate("You do not have necessary permissions to perform this action.")
            userName.value = user.email
            false
        } else {
            _dataState.value = DataState.SaveSuccess(
                LoginCredentials(user.email, user.password, user.id, user.name, user.permissions, user.role)
            )
            true
        }
    }

    sealed class AuthMethod {
        data object AuthByPassword : AuthMethod()
        data class AuthByPattern(val pattern: ArrayList<Int>) : AuthMethod()
    }
}