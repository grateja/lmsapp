package com.vag.lmsapp.app.expenses

import androidx.lifecycle.*
import com.vag.lmsapp.app.expenses.advanced_filter.ExpensesAdvancedFilter
import com.vag.lmsapp.room.repository.ExpensesRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.ResultCount
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel

@Inject
constructor(
    private val repository: ExpensesRepository
) : ListViewModel<ExpenseItemFull, ExpensesAdvancedFilter>() {

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
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

            val filterParams = filterParams.value ?: ExpensesAdvancedFilter()

            repository.filter(keyword.value, filterParams, page).let {
                setResult(it.items, it.count, reset)
                loading.value = false
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: ExpensesAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }
}