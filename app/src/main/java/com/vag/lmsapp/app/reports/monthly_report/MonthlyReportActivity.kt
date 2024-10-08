package com.vag.lmsapp.app.reports.monthly_report

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.calendar.CalendarReportActivity
import com.vag.lmsapp.app.reports.summary_report.SummaryReportActivity
import com.vag.lmsapp.app.reports.yearly_report.YearlyReportActivity
import com.vag.lmsapp.databinding.ActivityMonthlyReportBinding
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.setGridLayout
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MonthlyReportActivity : AppCompatActivity() {
    companion object {
        const val YEAR = "year"
    }
    private lateinit var binding: ActivityMonthlyReportBinding
    private val viewModel: MonthlyReportViewModel by viewModels()
    private val adapter = MonthlyResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_monthly_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerMonths.adapter = adapter

        binding.recyclerMonths.setGridLayout(
            this,
            200.dp
        )

        subscribeListeners()
        subscribeEvents()
        intent.getIntExtra(YEAR, LocalDate.now().year).let {
            viewModel.setYear(it)
        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is MonthlyReportViewModel.NavigationState.LoadResult -> {
                    adapter.setData(it.items)
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            val intent = Intent(this, CalendarReportActivity::class.java).apply {
                putExtra(Constants.MONTHLY_RESULT, it)
            }
            startActivity(intent)
        }
    }
}