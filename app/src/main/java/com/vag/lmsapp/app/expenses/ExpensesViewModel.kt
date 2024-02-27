package com.vag.lmsapp.app.expenses

import androidx.lifecycle.*
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.ExpensesRepository
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
) : ListViewModel<ExpenseItemFull, BaseFilterParams>() {
    private val _dateFilter = MutableLiveData<DateFilter?>()
    val dateFilter: LiveData<DateFilter?> = _dateFilter
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true
            delay(500)
            keyword.value?.let {
                items.value = repository.filter(it, _dateFilter.value)
            }
        }
    }

    fun setDates(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }

    fun clearDates() {
        _dateFilter.value = null
    }

    fun showDatePicker() {
        _dateFilter.value.let {
            val dateFilter = it ?: DateFilter(LocalDate.now(), null)
            _navigationState.value = NavigationState.OpenDateFilter(dateFilter)
        }
    }

    override fun clearState() {
        _navigationState.value = NavigationState.StateLess
        super.clearState()
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateFilter(val dateFilter: DateFilter): NavigationState()
    }
}