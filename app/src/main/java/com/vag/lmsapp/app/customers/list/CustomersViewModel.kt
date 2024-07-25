package com.vag.lmsapp.app.customers.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.customers.list.advanced_filter.CustomersAdvancedFilter
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.util.EnumSortDirection
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.ResultCount
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
) : ListViewModel<CustomerListItem, CustomersAdvancedFilter>() {

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

            val advancedFilter = filterParams.value ?: CustomersAdvancedFilter()

            loading.value = true

            repository.getListItems(
                keyword.value,
                page,
                advancedFilter
            ).let {
                setResult(it.result, it.count, reset)
                loading.value = false
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: CustomersAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }
}