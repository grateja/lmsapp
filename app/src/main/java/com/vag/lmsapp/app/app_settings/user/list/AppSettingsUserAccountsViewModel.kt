package com.vag.lmsapp.app.app_settings.user.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.app_settings.user.UserPreview
import com.vag.lmsapp.app.app_settings.user.list.advanced_filter.UserAccountAdvancedFilter
import com.vag.lmsapp.model.Role
import com.vag.lmsapp.room.repository.UserRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppSettingsUserAccountsViewModel

@Inject
constructor(
    private val repository: UserRepository
) : ListViewModel<UserPreview, UserAccountAdvancedFilter>() {
//    private val _role = MutableLiveData<Role?>()
//    val role: LiveData<Role?> = _role
//
//    val users = _role.switchMap { repository.getByRoleAsLiveData(it) }
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _authorizedId = MutableLiveData<UUID>()
    val authorizedUser = _authorizedId.switchMap { repository.getByIdAsLiveData(it) }

    fun setRole(role: Role?) {
        val filter = filterParams.value ?: UserAccountAdvancedFilter()
        setFilterParams(filter.apply {
            this.role = role
        })
        filter(true)
    }

    override fun filter(reset: Boolean) {
        job?.let {
            loading.value = false
            it.cancel()
        }

        job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page = 1
            }

            loading.value = true
            val filter = filterParams.value ?: UserAccountAdvancedFilter()
            repository.filter(keyword.value, page, filter).let {
                setResult(it.result, it.resultCount, reset)
                loading.value = false
                println("query result")
                println(it)
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: UserAccountAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }

    fun setUserId(userId: UUID) {
        _authorizedId.value = userId
    }

    fun openPreview(userId: UUID) {
        _authorizedId.value?.let {
            _navigationState.value = NavigationState.OpenPreview(it, userId)
        }
    }

    fun clearNavigationState() {
        _navigationState.value = NavigationState.Stateless
    }

    fun openAdd() {
        _authorizedId.value?.let {
            _navigationState.value = NavigationState.OpenAdd(it)
        }
    }

    sealed class NavigationState {
        data object Stateless: NavigationState()
        data class OpenPreview(val authorized: UUID, val userId: UUID): NavigationState()
        data class OpenAdd(val authorized: UUID): NavigationState()
    }
}