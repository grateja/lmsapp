package com.vag.lmsapp.app.shared_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.app.dashboard.data.DateFilter
import java.time.LocalDate

class BottomSheetDateRangePickerViewModel: ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState
    val dateFrom = MutableLiveData<LocalDate?>()
    val dateTo = MutableLiveData<LocalDate?>()

    fun setInitialDates(dateFilter: DateFilter) {
        dateFrom.value = dateFilter.dateFrom
        dateTo.value = dateFilter.dateTo
    }

    fun browseDateFrom() {
        val from = dateFrom.value ?: LocalDate.now()
        _navigationState.value = NavigationState.BrowseDateFrom(from)
    }

    fun browseDateTo() {
        val to = dateTo.value ?: dateFrom.value ?: LocalDate.now()
        _navigationState.value = NavigationState.BrowseDateTo(to)
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun setDateFrom(localDate: LocalDate) {
        dateFrom.value = localDate
    }

    fun setDateTo(localDate: LocalDate) {
        dateTo.value = localDate
    }

    fun submit() {
        val dateFrom = dateFrom.value ?: dateTo.value ?: return
        var dateTo = dateTo.value
        if(dateTo != null && dateTo.isEqual(dateFrom)) {
            dateTo = null
        }

        _navigationState.value = NavigationState.Submit(
            DateFilter(
                dateFrom,
                dateTo
            )
        )
    }

    fun switchDates() {
        val from = dateFrom.value
        val to = dateTo.value

        dateFrom.value = to
        dateTo.value = from
    }

    fun clearTo() {
        dateTo.value = null
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class BrowseDateFrom(val dateFrom: LocalDate): NavigationState()
        data class BrowseDateTo(val dateTo: LocalDate): NavigationState()
        data class Submit(val dateFilter: DateFilter): NavigationState()
    }
}