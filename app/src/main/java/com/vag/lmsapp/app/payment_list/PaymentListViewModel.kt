package com.vag.lmsapp.app.payment_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.payment_list.advanced_filter.JobOrderPaymentAdvancedFilter
import com.vag.lmsapp.room.entities.EntityJobOrderPaymentListItem
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.util.ResultCount
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel

@Inject
constructor (
    private val repository: PaymentRepository
): ListViewModel<EntityJobOrderPaymentListItem, JobOrderPaymentAdvancedFilter>() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

//    private val _dateFilter = MutableLiveData<DateFilter?>()
//    val dateFilter: LiveData<DateFilter?> = _dateFilter

    private val _total = MutableLiveData<ResultCount?>()
    val total: LiveData<ResultCount?> = _total

//    val orderBy = MutableLiveData("Date Paid")
//    val sortDirection = MutableLiveData(EnumSortDirection.DESC)

    fun setDateRange(dateFilter: DateFilter) {
//        _dateFilter.value = dateFilter
        filterParams.value = filterParams.value.apply {
            this?.dateFilter = dateFilter
        }
    }

    override fun filter(reset: Boolean) {
        job?.cancel()

        job = viewModelScope.launch {
            delay(500)

            val keyword = keyword.value
            if(reset) {
                page.value = 1
            }

            val filterParams = filterParams.value

            val page = page.value ?: 1
            val dateFilter = filterParams?.dateFilter
            val orderBy = filterParams?.orderBy
            val sortDirection = filterParams?.sortDirection

            repository.queryResult(keyword, page, dateFilter, orderBy, sortDirection).let {
                _dataState.value = DataState.LoadItems(it.items, reset)
                _total.value = it.resultCount
            }
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
    }

    fun clearDates() {
        filterParams.value = filterParams.value.apply {
            this?.dateFilter = null
        }
    }

//    fun showDatePicker() {
//        _dateFilter.value.let {
//            val dateFilter = it ?: DateFilter(LocalDate.now(), null)
//            _navigationState.value = NavigationState.OpenDateFilter(dateFilter)
//        }
//    }

    override fun clearState() {
        _navigationState.value = NavigationState.StateLess
        super.clearState()
    }

    fun showAdvancedFilter() {
        filterParams.value.let {
            _navigationState.value = NavigationState.ShowAdvancedFilter(it ?: JobOrderPaymentAdvancedFilter())
        }
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateFilter(val dateFilter: DateFilter): NavigationState()
        data class ShowAdvancedFilter(val filter: JobOrderPaymentAdvancedFilter): NavigationState()
    }
}