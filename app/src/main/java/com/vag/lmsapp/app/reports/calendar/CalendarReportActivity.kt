package com.vag.lmsapp.app.reports.calendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityCalendarReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarReportBinding
    private val viewModel: CalendarReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}