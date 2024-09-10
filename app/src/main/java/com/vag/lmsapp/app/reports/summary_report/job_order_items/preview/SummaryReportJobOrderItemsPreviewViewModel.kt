package com.vag.lmsapp.app.reports.summary_report.job_order_items.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SummaryReportJobOrderItemsPreviewViewModel

@Inject
constructor(
    private val repository: DailyReportRepository
): ViewModel() {
    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate> = _date
}