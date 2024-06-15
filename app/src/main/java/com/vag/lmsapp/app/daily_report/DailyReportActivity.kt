package com.vag.lmsapp.app.daily_report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.ActivityDailyReportBinding
import dagger.hilt.android.AndroidEntryPoint
import android.icu.util.Calendar
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import androidx.lifecycle.Observer
import com.vag.lmsapp.app.export_options.ExportOptionsActivity
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import java.time.LocalDate

@AndroidEntryPoint
class DailyReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyReportBinding
    private val viewModel: DailyReportViewModel by viewModels()
    private val daysAdapter = DaysAdapter()

    private val years by lazy {
        (2020..2040).toList().map { it.toString() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_report)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerDays)

        binding.recyclerDays.adapter = daysAdapter

        subscribeEvents()
        subscribeListeners()

        val calendar = Calendar.getInstance()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearIndex = years.indexOf(currentYear.toString())

        binding.textToday.text = calendar.get(Calendar.DATE).toString()

        binding.spinnerMonth.setSelection(calendar.get(Calendar.MONTH))

        binding.spinnerYear.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, years).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerYear.setSelection(yearIndex)
    }

    private fun subscribeListeners() {
        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is DailyReportViewModel.NavigationState.OpenExportOptions -> {
                    val intent = Intent(this, ExportOptionsActivity::class.java).apply {
                        putExtra(DATE_RANGE_FILTER, it.dateFilter)
                    }
                    startActivity(intent)
                    viewModel.resetState()
                }
                else -> {}
            }
        })
    }

    private fun subscribeEvents() {
        binding.buttonToday.setOnClickListener {
            LocalDate.now().let {
                daysAdapter.setDate(it)
                val yearIndex = years.indexOf(it.year.toString())
                val monthIndex = it.monthValue - 1

                binding.spinnerYear.setSelection(yearIndex)
                binding.spinnerMonth.setSelection(monthIndex)
            }
        }
        binding.spinnerMonth.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                daysAdapter.setMonth(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.spinnerYear.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                daysAdapter.setYear(years[index].toInt())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
        daysAdapter.setOnDaySelectedListener {
            viewModel.setDate(it)
        }
        binding.buttonCardExport.setOnClickListener {
            viewModel.openExportToExcelDialog()
        }

        activityLauncher.onOk = {
            val uri = it.data?.data
            if(uri != null) {
                viewModel.prepareWorkBook(uri)
                // Create workbook and populate data
                val workbook = XSSFWorkbook()
                val summarySheet = workbook.createSheet("Summary")
                val jobOrdersSheet = workbook.createSheet("Job Orders")
                val joSimplifiedSheet = workbook.createSheet("JO Simplified")
                val row = summarySheet.createRow(0)
                val cell = row.createCell(0)
                cell.setCellValue("Hello from Kotlin!")
                cell.cellFormula = "1+1"

                // Open output stream and write data
                val outputStream = contentResolver.openOutputStream(uri)
                workbook.write(outputStream)
                outputStream?.close()

                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

                startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
            }
        }
    }

    private val activityLauncher = ActivityLauncher(this)

    private fun openCreateDialog(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        activityLauncher.launch(intent)
    }
}