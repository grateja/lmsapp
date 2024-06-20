package com.vag.lmsapp.app.export_options

import android.content.Intent
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
import com.github.chrisbanes.photoview.BuildConfig.APPLICATION_ID
import com.vag.lmsapp.R
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.databinding.ActivityExportOptionsBinding
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants.Companion.DATE_RANGE_FILTER
import com.vag.lmsapp.util.Constants.Companion.FILE_PROVIDER
import com.vag.lmsapp.util.DatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ExportOptionsActivity : AppCompatActivity() {
    companion object {
        const val BROWSE_DATE_FROM = "date_from"
        const val BROWSE_DATE_TO = "date_to"
    }

    private lateinit var binding: ActivityExportOptionsBinding
    private val viewModel: ExportOptionsViewModel by viewModels()

    private val datePicker = DatePicker(this)
    private val activityLauncher = ActivityLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_export_options)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent.getParcelableExtra<DateFilter>(DATE_RANGE_FILTER)?.let {
            viewModel.setDateFilter(it)
        }

        subscribeEvents()
        subscribeListeners()
    }

    private fun subscribeEvents() {
        binding.cardButtonDateFrom.setOnClickListener {
            viewModel.browseDateFrom()
        }

        binding.cardButtonDateTo.setOnClickListener {
            viewModel.browseDateTo()
        }

        binding.cardButtonOpen.setOnClickListener {
            viewModel.openPreview()
        }

        binding.cardButtonSave.setOnClickListener {
            viewModel.showCreateDialog()
        }

        binding.cardButtonSend.setOnClickListener {
            viewModel.prepareSend()
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
                viewModel.prepareSave(it)
            }
        }
    }

    private fun subscribeListeners() {
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

                is ExportOptionsViewModel.NavigationState.SaveWorkbook -> {
                    save(it)
                    viewModel.resetState()
                }

                is ExportOptionsViewModel.NavigationState.SendWorkbook -> {
                    send(it)
                    viewModel.resetState()
                }

                is ExportOptionsViewModel.NavigationState.OpenWorkbook -> {
                    open(it)
                    viewModel.resetState()
                }

                is ExportOptionsViewModel.NavigationState.Invalidate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }

                else -> {}
            }
        })
    }

    private fun open(it: ExportOptionsViewModel.NavigationState.OpenWorkbook) {
        val file = File(cacheDir, "${it.filename}.xlsx")
        FileOutputStream(file).use { outputStream ->
            it.workbook.write(outputStream)
        }
        it.workbook.close()

        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun send(it: ExportOptionsViewModel.NavigationState.SendWorkbook) {
        val file = File(cacheDir, "${it.filename}.xlsx")
        FileOutputStream(file).use { outputStream ->
            it.workbook.write(outputStream)
        }
        it.workbook.close()

        val uri = FileProvider.getUriForFile(this, FILE_PROVIDER, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun save(it: ExportOptionsViewModel.NavigationState.SaveWorkbook) {
        val outputStream = contentResolver.openOutputStream(it.uri)
        it.workbook.write(outputStream)
        outputStream?.close()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(
            it.uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"))
    }

    private fun openCreateDialog(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activityLauncher.launch(intent)
    }
}