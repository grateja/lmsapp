package com.vag.lmsapp.app.reports.yearly_report

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.app.reports.monthly_report.MonthlyReportActivity
import com.vag.lmsapp.app.reports.monthly_report.MonthlyReportActivity.Companion.YEAR
import com.vag.lmsapp.databinding.ActivityYearlyReportBinding
import com.vag.lmsapp.util.setGridLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YearlyReportActivity : AppCompatActivity() {
    private val viewModel: YearlyReportViewModel by viewModels()
    private lateinit var binding: ActivityYearlyReportBinding
    private val adapter = YearlyResultAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_yearly_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerViewYearly.adapter = adapter
        binding.recyclerViewYearly.setGridLayout(
            this, 200.dp
        )

        subscribeListeners()
        subscribeEvents()
    }

    private fun subscribeListeners() {
        viewModel.result.observe(this, Observer {
            adapter.setData(it)
        })
    }

    private fun subscribeEvents() {
        adapter.onItemClick = {
            val intent = Intent(this, MonthlyReportActivity::class.java).apply {
                putExtra(YEAR, it.year)
            }
            startActivity(intent)
        }
    }
}