package com.vag.lmsapp.app.reports.yearly_report

import androidx.lifecycle.ViewModel
import com.vag.lmsapp.room.repository.YearlyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class YearlyReportViewModel

@Inject
constructor(
    private val repository: YearlyReportRepository
): ViewModel() {
    val result = repository.yearly()
}