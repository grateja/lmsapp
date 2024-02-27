package com.vag.lmsapp.app.customers.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel

@Inject
constructor(
    private val repository: CustomerRepository
) : ListViewModel<CustomerListItem, BaseFilterParams>() {
    val total = MutableLiveData(0)
    val hideAllWithoutJo = MutableLiveData(false)

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

    override fun filter(reset: Boolean) {
        println("filtering keme")
        job?.cancel()

        job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page.value = 1
            }
            val filterParams = filterParams.value
            val keyword = keyword.value
            val page = page.value ?: 1
            val orderBy = filterParams?.orderBy
            val sortDirection = filterParams?.sortDirection
            val hideAllWithoutJo = hideAllWithoutJo.value ?: true
            val dateFilter = _dateFilter.value

            loading.value = true

            val items = repository.getListItems(
                keyword,
                orderBy,
                sortDirection,
                page,
                hideAllWithoutJo,
                dateFilter
            )
            _dataState.value = DataState.LoadItems(items.result, reset)
            total.value = items.count
            loading.value = false
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
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