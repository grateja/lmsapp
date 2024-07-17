package com.vag.lmsapp.app.customers.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.util.EnumSortDirection
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.ResultCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel

@Inject
constructor(
    private val repository: CustomerRepository
) : ViewModel() {
    private var _job: Job? = null
    private var _page = 1

    val keyword = MutableLiveData("")
    val total = MutableLiveData<ResultCount>()
    val hideAllWithoutJo = MutableLiveData(false)
    val sortDirection = MutableLiveData(EnumSortDirection.ASC)
    val orderBy = MutableLiveData("Name")

    private val _filterState = MutableLiveData<FilterState<CustomerListItem>>()
    val filterState: LiveData<FilterState<CustomerListItem>> = _filterState

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _dateFilter = MutableLiveData<DateFilter?>()
    val dateFilter: LiveData<DateFilter?> = _dateFilter

    fun setDateRange(dateFilter: DateFilter?) {
        _dateFilter.value = dateFilter
    }

    fun clearDates() {
        _dateFilter.value = null
    }

    fun filter(reset: Boolean) {
        _job?.cancel()

        _job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                _page = 1
            }
            val keyword = keyword.value
            val page = _page ?: 1
            val orderBy = orderBy.value ?: "Name"
            val sortDirection = sortDirection.value ?: EnumSortDirection.ASC
            val hideAllWithoutJo = hideAllWithoutJo.value ?: true
            val dateFilter = _dateFilter.value

            _loading.value = true

            val items = repository.getListItems(
                keyword,
                orderBy,
                sortDirection,
                page,
                hideAllWithoutJo,
                dateFilter
            )
            _filterState.value = FilterState.LoadItems(items.result, reset)
            total.value = items.count
            _loading.value = false
        }
    }

    fun loadMore() {
        ++_page
        filter(false)
    }

    fun showDatePicker() {
        _dateFilter.value.let {
            val dateFilter = it ?: DateFilter(LocalDate.now(), null)
            _navigationState.value = NavigationState.OpenDateFilter(dateFilter)
        }
    }

    fun setKeyword(keyword: String?) {
        this.keyword.value = keyword
    }
//
//    fun showAdvancedFilter() {
//        val filter = filterParams.value ?: CustomersAdvancedFilter()
//        _navigationState.value = NavigationState.OpenAdvancedFilter(filter)
//    }

    fun clearState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateFilter(val dateFilter: DateFilter): NavigationState()
//        data class OpenAdvancedFilter(val advancedFilter: CustomersAdvancedFilter): NavigationState()
    }
}