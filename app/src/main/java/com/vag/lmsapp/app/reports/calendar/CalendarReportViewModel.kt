package com.vag.lmsapp.app.reports.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.reports.monthly_report.MonthlyResult
import com.vag.lmsapp.room.repository.DailyReportRepository
import com.vag.lmsapp.util.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarReportViewModel

@Inject
constructor(
    private val repository: DailyReportRepository
): ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _monthResult = MutableLiveData<MonthlyResult>()
    val monthlyResult: LiveData<MonthlyResult> = _monthResult

    fun setMonth(monthlyResult: MonthlyResult) {
        _monthResult.value = monthlyResult

        viewModelScope.launch {
            _monthResult.value?.let {m ->
                val items = mutableListOf<DailyPlaceHolder>()
                val result = repository.daily(m.year, m.month.monthNumberStr)

                for(day in 1 until m.daysInMonth() + 1) {
                    items.add(
                        result.find { it.date.dayOfMonth == day }.let {
                            if(it != null) {
                                DailyPlaceHolder.HasResult(it)
                            } else {
                                DailyPlaceHolder.EmptyResult(day)
                            }
                        }
                    )
                }

                _navigationState.value = NavigationState.LoadResult(items, m.year, m.month.monthNumber)
            }
        }
    }

    fun clearState() {
        _navigationState.value = NavigationState.Stateless
    }

    fun openSummary() {
        _monthResult.value?.let {m ->
            val dateFilter = DateFilter(
                LocalDate.of(m.year, m.month.monthNumber, 1),
                LocalDate.of(m.year, m.month.monthNumber, m.daysInMonth())
            )
            _navigationState.value = NavigationState.OpenSummary(dateFilter)
        }
    }

    sealed class NavigationState {
        data object Stateless: NavigationState()
        data class LoadResult(val items: List<DailyPlaceHolder>, val year: Int, val month: Int): NavigationState()
        data class OpenSummary(val dateFilter: DateFilter): NavigationState()
    }
}