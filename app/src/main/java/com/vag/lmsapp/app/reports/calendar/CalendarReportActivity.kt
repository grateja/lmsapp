package com.vag.lmsapp.app.reports.calendar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.monthly_report.MonthlyResult
import com.vag.lmsapp.app.reports.summary_report.SummaryReportActivity
import com.vag.lmsapp.databinding.ActivityCalendarReportBinding
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.Constants.Companion.MONTHLY_RESULT
import com.vag.lmsapp.util.DateFilter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarReportBinding
    private val viewModel: CalendarReportViewModel by viewModels()
    private val adapter = CalendarAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerDays.adapter = adapter
        binding.recyclerDays.layoutManager = GridLayoutManager(this, 7)

        subscribeListeners()
        subscribeEvents()

        intent.getParcelableExtra<MonthlyResult>(MONTHLY_RESULT)?.let {
            viewModel.setMonth(it)
        }
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            val intent = Intent(this, SummaryReportActivity::class.java).apply {
                putExtra(DATE_RANGE_FILTER, DateFilter(it))
            }
            startActivity(intent)
        }

        binding.inclMonthlyResult.cardContainer.setOnClickListener {
            viewModel.openSummary()
        }
    }

    private fun subscribeListeners() {
        viewModel.monthlyResult.observe(this, Observer {
            binding.inclMonthlyResult.viewModel = it
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is CalendarReportViewModel.NavigationState.LoadResult -> {
                    adapter.setData(it.items, it.year, it.month)
                }

                is CalendarReportViewModel.NavigationState.OpenSummary -> {
                    val intent = Intent(this, SummaryReportActivity::class.java).apply {
                        putExtra(DATE_RANGE_FILTER, it.dateFilter)
                    }
                    startActivity(intent)
                }

                else -> {}
            }
        })
    }
}
