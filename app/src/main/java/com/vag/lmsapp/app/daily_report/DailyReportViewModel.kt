package com.vag.lmsapp.app.daily_report

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.app.daily_report.wash_dry_services.DailyReportWashDrySummary
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyReportViewModel

@Inject
constructor(
    private val dailyReportRepository: DailyReportRepository
): ViewModel(){
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _currentDate = MutableLiveData(LocalDate.now())
    val currentDate: LiveData<LocalDate> = _currentDate

    private val _dateFilter = MutableLiveData<DateFilter>()

    val jobOrder = _dateFilter.switchMap { dailyReportRepository.jobOrder(it) }

    val jobOrderPayment = _dateFilter.switchMap { dailyReportRepository.jobOrderPayment(it) }
    val jobOrderPaymentSummary = _dateFilter.switchMap { dailyReportRepository.jobOrderPaymentSummary(it) }

    val washServices = _dateFilter.switchMap { dailyReportRepository.washServices(it) }
    val dryServices = _dateFilter.switchMap { dailyReportRepository.dryServices(it) }
    val washDrySummary = MediatorLiveData<DailyReportWashDrySummary>().apply {
        fun update() {
            val washes = washServices.value?.sumOf { it.count } ?: 0
            val dries = dryServices.value?.sumOf { it.count } ?: 0
            value = DailyReportWashDrySummary(
                washes, dries, washes + dries
            )
        }
        addSource(washServices) {update()}
        addSource(dryServices) {update()}
    }

    val extras = _dateFilter.switchMap { dailyReportRepository.extras(it) }
    val extrasSum = MediatorLiveData<Int>().apply {
        addSource(extras) {
            value = it.sumOf { it.count }
        }
    }

    val detergents = _dateFilter.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.DETERGENT) }
    val fabCons = _dateFilter.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.FAB_CON) }
    val otherProducts = _dateFilter.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.OTHER) }
    val productsChemicalsSummary = _dateFilter.switchMap { dailyReportRepository.productsChemicalsSummary(it) }

    val delivery = _dateFilter.switchMap { dailyReportRepository.delivery(it) }
    val deliverySum = MediatorLiveData<Int>().apply {
        addSource(delivery) {
            value = it.sumOf { it.count }
        }
    }

    val machineUsages = _dateFilter.switchMap { dailyReportRepository.machineUsages(it) }
    val machineUsageCount = MediatorLiveData<Int>().apply {
        addSource(machineUsages) {
            value = it.sumOf { it.count }
        }
    }

    val expenses = _dateFilter.switchMap { dailyReportRepository.expenses(it) }
    val expensesTotal = MediatorLiveData<String>().apply {
        addSource(expenses) {
            val amount = it.sumOf { it.amount }
            value = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount)) + " total expenses"
        }
    }

    fun setDate(date: LocalDate) {
        _currentDate.value = date
        _dateFilter.value = DateFilter(date)
    }

    fun openExportToExcelDialog() {
//        val fileName = _dateFilter.value?.toString() ?: "filename"
//        _navigationState.value = NavigationState.SetUriFilename(fileName)
        _dateFilter.value?.let {
            _navigationState.value = NavigationState.OpenExportOptions(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun prepareWorkBook(uri: Uri) {
        val workbook = XSSFWorkbook()



        _navigationState.value = NavigationState.ExportContinue(workbook, uri)
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class SetUriFilename(val fileName: String): NavigationState()
        data class ExportContinue(val workBook: XSSFWorkbook, val uri: Uri): NavigationState()
        data class OpenExportOptions(val dateFilter: DateFilter): NavigationState()
    }
}