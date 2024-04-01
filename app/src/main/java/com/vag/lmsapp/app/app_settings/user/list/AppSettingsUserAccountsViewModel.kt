package com.vag.lmsapp.app.app_settings.user.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppSettingsUserAccountsViewModel

@Inject
constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _role = MutableLiveData<Role?>()
    val role: LiveData<Role?> = _role

    val users = _role.switchMap { repository.getByRoleAsLiveData(it) }

    fun setRole(role: Role?) {
        _role.value = role
    }
}