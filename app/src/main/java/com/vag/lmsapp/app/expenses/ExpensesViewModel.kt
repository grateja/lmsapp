package com.vag.lmsapp.app.expenses

import androidx.lifecycle.*
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.expenses.advanced_filter.ExpensesAdvancedFilter
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ExpensesRepository
import com.vag.lmsapp.util.ResultCount
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel

@Inject
constructor(
    private val repository: ExpensesRepository
) : ListViewModel<ExpenseItemFull, ExpensesAdvancedFilter>() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _total = MutableLiveData<ResultCount>()
    val total: LiveData<ResultCount> = _total

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        if(reset) {
            page.value = 1
        }

        job = viewModelScope.launch {
            loading.value = true

            delay(500)
            val page = page.value ?: 1
            val filterParams = filterParams.value ?: ExpensesAdvancedFilter()

            keyword.value?.let {
                repository.filter(it, filterParams, page).let {
                    _dataState.value = DataState.LoadItems(it.items, reset)
                    _total.value = it.count
                }
            }
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
    }

    override fun clearState() {
        _navigationState.value = NavigationState.StateLess
        super.clearState()
    }

    fun showAdvancedFilter() {
        filterParams.value.let {
            _navigationState.value = NavigationState.ShowAdvancedFilter(it ?: ExpensesAdvancedFilter())
        }
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class ShowAdvancedFilter(val filter: ExpensesAdvancedFilter): NavigationState()
    }
}