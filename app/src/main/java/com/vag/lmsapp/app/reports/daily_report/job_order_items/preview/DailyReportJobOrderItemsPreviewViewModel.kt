package com.vag.lmsapp.app.reports.daily_report.job_order_items.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyReportJobOrderItemsPreviewViewModel

@Inject
constructor(
    private val repository: DailyReportRepository
): ViewModel() {
    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate> = _date
}