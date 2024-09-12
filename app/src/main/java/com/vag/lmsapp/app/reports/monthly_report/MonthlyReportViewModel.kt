package com.vag.lmsapp.app.reports.monthly_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.MonthlyReportRepository
import com.vag.lmsapp.util.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyReportViewModel

@Inject
constructor(
    private val repository: MonthlyReportRepository
): ViewModel() {
    private val _year = MutableLiveData<Int>()
    val year: LiveData<Int> = _year

    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    fun setYear(year: Int) {
        _year.value = year
        viewModelScope.launch {
            val items = mutableListOf<MonthlyPlaceHolder>()
            val result = repository.monthly(year)
            EnumMonths.entries.forEach { month ->
                val monthly = result.find { it.month.monthNumberStr == month.monthNumberStr }
                items.add(
                    result.find { it.month.monthNumberStr == month.monthNumberStr }.let {
                        if(it != null) {
                            MonthlyPlaceHolder.HasResult(it)
                        } else {
                            MonthlyPlaceHolder.EmptyResult(month)
                        }
                    }
                )
            }

            _navigationState.value = NavigationState.LoadResult(items)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.Stateless
    }

    sealed class NavigationState {
        data object Stateless: NavigationState()
        data class LoadResult(val items: List<MonthlyPlaceHolder>): NavigationState()
        data class OpenSummary(val dateFilter: DateFilter): NavigationState()
    }
}