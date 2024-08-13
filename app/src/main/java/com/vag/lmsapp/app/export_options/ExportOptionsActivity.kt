package com.vag.lmsapp.app.export_options

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.export_options.data.ExportSelectionsAdapter
import com.vag.lmsapp.app.export_options.data.ProgressReport
import com.vag.lmsapp.databinding.ActivityExportOptionsBinding
import com.vag.lmsapp.services.ExcelExportService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.Constants.Companion.FILE_PROVIDER
import com.vag.lmsapp.util.DateFilter
import com.vag.lmsapp.util.DatePicker
import com.vag.lmsapp.util.EnumCheckboxState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class ExportOptionsActivity : AppCompatActivity() {
    companion object {
        const val BROWSE_DATE_FROM = "date_from"
        const val BROWSE_DATE_TO = "date_to"
    }

    private lateinit var binding: ActivityExportOptionsBinding
    private val viewModel: ExportOptionsViewModel by viewModels()

    private val exportSelectionsAdapter = ExportSelectionsAdapter()

    private val datePicker = DatePicker(this)
    private val activityLauncher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_export_options)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerItemCheckbox.adapter = exportSelectionsAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(ExcelExportService.running) {
            ExcelExportService.check(this)
        } else {
            stop()
//            intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER)?.let {
//                viewModel.setDateFilter(it)
//            }
        }

        subscribeEvents()
        subscribeListeners()
        setupAnimator()

//        FileExistenceLiveData(filesDir.toString(), ExcelExportService.FILENAME).observe(this, Observer {
//            binding.controls.visibility = if(it) View.VISIBLE else View.GONE
//            if(it) {
//                binding.buttonCreateFile.text = Instant.ofEpochMilli(File(filesDir, ExcelExportService.FILENAME).lastModified()).setMomentAgo()
//            }
//        })

//        fileObserver =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                object: FileObserver(filesDir, CREATE or DELETE) {
//                    override fun onEvent(p0: Int, p1: String?) {
//                        println("file created or deleted $p0 $p1")
//                    }
//                }
//            } else {
//                object: FileObserver(filesDir.absolutePath, CREATE or DELETE) {
//                    override fun onEvent(p0: Int, p1: String?) {
//                        println("file created or deleted $p0 $p1")
//                    }
//                }
//            }
    }

//    private lateinit var fileObserver: FileObserver

    private val _colorAnimator = ValueAnimator.ofFloat(0f, .5f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }

    private fun setupAnimator() {
        val colorEvaluator = ArgbEvaluator()
        val colorStart = getColor(R.color.primary)
        val colorEnd = getColor(R.color.secondary)

        _colorAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            val color = colorEvaluator.evaluate(fraction, colorStart, colorEnd) as Int
            binding.buttonCreateFile.setBackgroundColor(color)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ExcelExportService.ACTION_FILE_CREATED).apply {
            addAction(ExcelExportService.ACTION_PENDING)
            addAction(ExcelExportService.ACTION_PROGRESS)
            addAction(ExcelExportService.ACTION_CANCEL)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

//        checkFile()
//        fileObserver.startWatching()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
//        fileObserver.stopWatching()
    }

//    private fun checkFile() {
//        File(filesDir, ExcelExportService.FILENAME).exists().let {
//            binding.controls.visibility = if(it) View.VISIBLE else View.GONE
//        }
//    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                ExcelExportService.ACTION_PENDING -> {
                    intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER)?.let {
                        viewModel.setDateFilter(it)
                    }
                }

                ExcelExportService.ACTION_FILE_CREATED -> {
                    Toast.makeText(applicationContext, "File created", Toast.LENGTH_SHORT).show()
                    stop()
                }

                ExcelExportService.ACTION_PROGRESS -> {
                    intent.getParcelableExtra<ProgressReport>(ExcelExportService.PROGRESS_EXTRA).let {
                        viewModel.setProgress(it)
                    }
                }

                ExcelExportService.ACTION_CANCEL -> {
                    viewModel.setProgress(null)
                    Toast.makeText(applicationContext, "Operation canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun stop() {
        stopService(Intent(applicationContext, ExcelExportService::class.java))
        viewModel.setProgress(null)
    }

    private fun subscribeEvents() {
        exportSelectionsAdapter.selectionChanged = { items, state ->
            viewModel.setSelection(items)
            binding.checkboxSelectAll.isChecked = state == EnumCheckboxState.CHECKED
            binding.textSelectAll.text = when (state) {
                EnumCheckboxState.CHECKED -> "Deselect All"
                EnumCheckboxState.UNCHECKED -> "Select All"
                EnumCheckboxState.INDETERMINATE -> "Select All"
            }
        }

        binding.checkboxSelectAll.setOnClickListener {
            exportSelectionsAdapter.toggle()
        }

        binding.cardButtonDateFrom.setOnClickListener {
            viewModel.browseDateFrom()
        }

        binding.cardButtonDateTo.setOnClickListener {
            viewModel.browseDateTo()
        }

        binding.buttonCreateFile.setOnClickListener {
            viewModel.createFile()
        }

        binding.buttonCancel.setOnClickListener {
            cancelExport()
        }

        binding.cardButtonOpen.setOnClickListener {
            open()
        }

        binding.cardButtonSave.setOnClickListener {
            viewModel.showCreateDialog()
        }

        binding.cardButtonSend.setOnClickListener {
            send()
        }

        datePicker.setOnDateTimeSelectedListener { localDate, tag ->
            if(tag == BROWSE_DATE_FROM) {
                viewModel.setDateFrom(localDate)
            } else if(tag == BROWSE_DATE_TO) {
                viewModel.setDateTo(localDate)
            }
        }

        activityLauncher.onOk = {
            it.data?.data?.let {
                save(it)
            }
        }
    }

    private fun subscribeListeners() {
        viewModel.requireRefresh.observe(this, Observer {
            if(it) {
                _colorAnimator.start()
            } else {
                _colorAnimator.cancel()
            }
        })
        viewModel.oldDateFilter.observe(this, Observer {
            viewModel.setDateFilter(it)
        })
        viewModel.jobOrdersCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_JOB_ORDER, it)
        })
        viewModel.customersCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_NEW_CUSTOMERS, it)
        })
        viewModel.expensesCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_EXPENSES, it)
        })
        viewModel.machineUsagesCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_MACHINE_USAGES, it)
        })
        viewModel.unpaidJobOrdersCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_UNPAID_JOB_ORDERS, it)
        })
        viewModel.deliveryChargesCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_DELIVERY_CHARGES, it)
        })
        viewModel.jobOrdersExtrasCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_EXTRAS, it)
        })
        viewModel.jobOrdersProductsCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_PRODUCTS_CHEMICALS, it)
        })
        viewModel.jobOrdersServicesCount.observe(this, Observer {
            exportSelectionsAdapter.updateCount(ExcelExportService.INCLUDES_WASH_DRY_SERVICES, it)
        })

        viewModel.navigationState.observe(this, Observer {
            when(it) {
                is ExportOptionsViewModel.NavigationState.BrowseDateFrom -> {
                    datePicker.show(it.localDate, BROWSE_DATE_FROM)
                    viewModel.resetState()
                }

                is ExportOptionsViewModel.NavigationState.BrowseDateTo -> {
                    datePicker.show(it.localDate, BROWSE_DATE_TO)
                    viewModel.resetState()
                }

                is ExportOptionsViewModel.NavigationState.ShowCreateDialog -> {
                    openCreateDialog(it.filename)
                    viewModel.resetState()
                }

//                is ExportOptionsViewModel.NavigationState.SaveWorkbook -> {
////                    save(it)
//                    viewModel.resetState()
//                }
//
//                is ExportOptionsViewModel.NavigationState.SendWorkbook -> {
//                    send(it)
//                    viewModel.resetState()
//                }
//
//                is ExportOptionsViewModel.NavigationState.OpenWorkbook -> {
//                    open(it)
//                    viewModel.resetState()
//                }
//
                is ExportOptionsViewModel.NavigationState.StartExportService -> {
                    createFile(it.dateFilter, it.selections)
                    viewModel.resetState()
                }
//
                is ExportOptionsViewModel.NavigationState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun createFile(dateFilter: DateFilter, inclusions: ArrayList<String>) {
        val intent = Intent(this, ExcelExportService::class.java).apply {
            action = ExcelExportService.ACTION_START
            putExtra(DATE_RANGE_FILTER, dateFilter)
            putStringArrayListExtra(ExcelExportService.INCLUSIONS, inclusions)
        }
        startForegroundService(intent)
    }

    private fun open() {
//        val file = File(cacheDir, "${it.filename}.xlsx")
//        FileOutputStream(file).use { outputStream ->
//            it.workbook.write(outputStream)
//        }
//        it.workbook.close()

        val file = File(filesDir, ExcelExportService.FILENAME_XLSX)
        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun send() {
//        val file = File(cacheDir, "${it.filename}.xlsx")
//        FileOutputStream(file).use { outputStream ->
//            it.workbook.write(outputStream)
//        }
//        it.workbook.close()
        val file = File(filesDir, ExcelExportService.FILENAME_XLSX)

        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun save(uri: Uri) {
//        val outputStream = contentResolver.openOutputStream(it.uri)
//        it.workbook.write(outputStream)
//        outputStream?.close()
        val file = File(filesDir, ExcelExportService.FILENAME_XLSX)

//        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)

        val inputStream: InputStream?
        val outputStream: OutputStream?

        try {
//            val file = File(filesDir, "filename.txt") // Your file in filesDir
            inputStream = FileInputStream(file)
            outputStream = contentResolver.openOutputStream(uri)

            if (outputStream != null) {
                val buffer = ByteArray(1024)
                var length: Int

                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                inputStream.close()
                outputStream.close()

                Toast.makeText(this, "File copied successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to copy file", Toast.LENGTH_SHORT).show()
        }

//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        intent.setDataAndType(
//            uri,
//            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//        )
//
//        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun openCreateDialog(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activityLauncher.launch(intent)
    }

    private fun cancelExport() {
        val intent = Intent(this, ExcelExportService::class.java).apply {
            action = ExcelExportService.ACTION_CANCEL
        }
        startForegroundService(intent)
    }
}