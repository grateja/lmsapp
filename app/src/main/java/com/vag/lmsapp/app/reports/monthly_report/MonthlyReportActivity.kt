package com.vag.lmsapp.app.reports.monthly_report

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.summary_report.SummaryReportActivity
import com.vag.lmsapp.databinding.ActivityMonthlyReportBinding
import com.vag.lmsapp.util.setGridLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthlyReportActivity : AppCompatActivity() {
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

        viewModel.setYear(2024)
    }

    private fun subscribeListeners() {
        viewModel.result.observe(this, Observer {
            adapter.setData(it)
        })
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            val intent = Intent(this, SummaryReportActivity::class.java).apply {
                putExtra(SummaryReportActivity.CURRENT_MONTH_EXTRA, it.month.monthNumber.toInt())
            }
            startActivity(intent)
        }
    }
}