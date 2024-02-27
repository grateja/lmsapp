package com.vag.lmsapp.app.payment_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.room.entities.EntityJobOrderPaymentListItem
import com.vag.lmsapp.room.entities.QueryAggrResult
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel

@Inject
constructor (
    private val repository: PaymentRepository
): ListViewModel<EntityJobOrderPaymentListItem, Nothing>() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _dateFilter = MutableLiveData<DateFilter?>()
    val dateFilter: LiveData<DateFilter?> = _dateFilter

    private val _aggrResult = MutableLiveData<QueryAggrResult?>()
    val aggrResult: LiveData<QueryAggrResult?> = _aggrResult

    fun setDateRange(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }

    override fun filter(reset: Boolean) {
        job?.cancel()

        job = viewModelScope.launch {
            delay(500)

            val keyword = keyword.value
            if(reset) {
                page.value = 1
            }

            val page = page.value ?: 1
            val dateFilter = dateFilter.value

            repository.queryResult(keyword, page, dateFilter).let {
                _dataState.value = DataState.LoadItems(it.items, reset)
                _aggrResult.value = it.aggResult
            }
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
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