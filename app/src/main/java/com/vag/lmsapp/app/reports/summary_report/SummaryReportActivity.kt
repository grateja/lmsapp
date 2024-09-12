package com.vag.lmsapp.app.reports.summary_report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivitySummaryReportBinding
import dagger.hilt.android.AndroidEntryPoint
import android.icu.util.Calendar
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.expenses.ExpensesActivity
import com.vag.lmsapp.app.export_options.ExportOptionsActivity
import com.vag.lmsapp.app.joborders.list.JobOrderListActivity
import com.vag.lmsapp.app.payment_list.PaymentListActivity
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilter
import com.vag.lmsapp.app.machines.preview.MachinePreviewActivity
import com.vag.lmsapp.app.reports.summary_report.job_order_items.BottomSheetJobOrderItemPreviewFragment
import com.vag.lmsapp.util.Constants.Companion.ADVANCED_FILTER
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.DatePicker

@AndroidEntryPoint
class SummaryReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryReportBinding
    private val viewModel: SummaryReportViewModel by viewModels()
    private val daysAdapter = DaysAdapter()

    private val years by lazy {
        (2020..2040).toList().map { it.toString() }
    }

    private val datePicker = DatePicker(this).apply {
        setOnDateTimeSelectedListener { localDate, s ->
            viewModel.setDateFilter(
                DateFilter(localDate)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_summary_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(binding.recyclerDays)

//        binding.recyclerDays.adapter = daysAdapter

        subscribeEvents()
        subscribeListeners()

        val calendar = Calendar.getInstance()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearIndex = years.indexOf(currentYear.toString())

//        binding.textToday.text = calendar.get(Calendar.DATE).toString()
//
//        binding.spinnerMonth.setSelection(calendar.get(Calendar.MONTH))
//
//        binding.spinnerYear.adapter = ArrayAdapter(this,
//            android.R.layout.simple_spinner_item, years).apply {
//            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        }
//        binding.spinnerYear.setSelection(yearIndex)

        intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER).let {
            viewModel.setDateFilter(it ?: DateFilter())
        }

//        intent.getIntExtra(CURRENT_MONTH_EXTRA, -1).let {
//            if(it > 0) {
//                daysAdapter.setMonth(it)
//            }
//        }
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is SummaryReportViewModel.NavigationState.OpenDatePicker -> {
                    datePicker.show(it.date)
                }

                is SummaryReportViewModel.NavigationState.OpenExportOptions -> {
                    val intent = Intent(this, ExportOptionsActivity::class.java).apply {
                        putExtra(DATE_RANGE_FILTER, it.date)
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is SummaryReportViewModel.NavigationState.OpenJobOrders -> {
                    val intent = Intent(this, JobOrderListActivity::class.java).apply {
                        putExtra(ADVANCED_FILTER, JobOrderListAdvancedFilter(
                            dateFilter = it.date)
                        )
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is SummaryReportViewModel.NavigationState.OpenPayments -> {
                    val intent = Intent(this, PaymentListActivity::class.java).apply {
                        val dateRange = it.date
                        putExtra(DATE_RANGE_FILTER, dateRange)
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is SummaryReportViewModel.NavigationState.OpenExpenses -> {
                    val intent = Intent(this, ExpensesActivity::class.java).apply {
                        val dateRange = it.date
                        putExtra(DATE_RANGE_FILTER, dateRange)
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is SummaryReportViewModel.NavigationState.OpenMachineUsage -> {
                    val intent = Intent(this, MachinePreviewActivity::class.java).apply {
                        val dateRange = it.date
                        putExtra(DATE_RANGE_FILTER, dateRange)
                        putExtra(MachinePreviewActivity.MACHINE_TYPE_EXTRA, it.machineType as Parcelable)
                        putExtra(MachinePreviewActivity.SERVICE_TYPE_EXTRA, it.serviceType as Parcelable)
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }

                is SummaryReportViewModel.NavigationState.OpenJobOrderItems -> {
                    BottomSheetJobOrderItemPreviewFragment
                        .newInstance(it.title)
                        .show(supportFragmentManager, "open_washes")
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.appBar.setOnClickListener {
            viewModel.openDatePicker()
        }
        binding.buttonBrowseNext.setOnClickListener {
            viewModel.browseNext()
        }
        binding.buttonBrowsePrev.setOnClickListener {
            viewModel.browsePrev()
        }
//        binding.buttonToday.setOnClickListener {
//            LocalDate.now().let {
//                daysAdapter.setDate(it)
//                val yearIndex = years.indexOf(it.year.toString())
//                val monthIndex = it.monthValue - 1
//
//                binding.spinnerYear.setSelection(yearIndex)
//                binding.spinnerMonth.setSelection(monthIndex)
//            }
//        }
//        binding.spinnerMonth.onItemSelectedListener = object: OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                daysAdapter.setMonth(p2)
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) { }
//        }

//        binding.spinnerYear.onItemSelectedListener = object: OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
//                daysAdapter.setYear(years[index].toInt())
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) { }
//        }
//        daysAdapter.setOnDaySelectedListener {
//            viewModel.setDateFilter(it)
//        }
        binding.buttonCardExport.setOnClickListener {
            viewModel.openExportToExcelDialog()
        }

//        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//            when {
//                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
//                    binding.currentDate.visibility = View.VISIBLE
//                }
//
//                else -> {
//                    binding.currentDate.visibility = View.GONE
//                }
//            }
//        }
    }

    companion object {
        const val CURRENT_YEAR_EXTRA = "current_year_extra"
        const val CURRENT_MONTH_EXTRA = "current_month_extra"
    }
}