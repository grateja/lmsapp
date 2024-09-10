package com.vag.lmsapp.app.reports.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.app.reports.monthly_report.MonthlyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalendarReportViewModel

@Inject
constructor(

): ViewModel() {
    private val _monthResult = MutableLiveData<MonthlyResult>()
    val monthlyResult: LiveData<MonthlyResult> = _monthResult

    fun setMonth(monthlyResult: MonthlyResult) {
        _monthResult.value = monthlyResult
    }


}