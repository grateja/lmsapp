package com.vag.lmsapp.app.reports.monthly_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.room.repository.MonthlyReportRepository
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

    private val _result = MutableLiveData<List<MonthlyResult>>()
    val result: LiveData<List<MonthlyResult>> = _result

    fun setYear(year: Int) {
        _year.value = year
        viewModelScope.launch {
            val items = mutableListOf<MonthlyResult>()
            val jobOrders = repository.jobOrders(year)
            val customers = repository.customers(year)
            val expenses = repository.expenses(year)
            EnumMonths.entries.forEach { month ->
                val jobOrder = jobOrders.find { month.monthNumber == it.month.monthNumber }
                val customer = customers.find { month.monthNumber == it.month.monthNumber }
                val expense = expenses.find { month.monthNumber == it.month.monthNumber }
                items.add(
                    MonthlyResult(
                        month,
                        customer?.count ?: 0,
                        jobOrder?.count ?: 0,
                        jobOrder?.price ?: 0f,
                        expense?.price ?: 0f
                    )
                )
            }

            _result.value = items
        }
    }
}