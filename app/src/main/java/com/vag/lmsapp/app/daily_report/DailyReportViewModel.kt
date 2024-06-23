package com.vag.lmsapp.app.daily_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.app.daily_report.wash_dry_services.DailyReportWashDrySummary
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _date = MutableLiveData(LocalDate.now())
    val currentDate: LiveData<LocalDate> = _date

    val jobOrder = _date.switchMap { dailyReportRepository.jobOrder(it) }

    val jobOrderPayment = _date.switchMap { dailyReportRepository.jobOrderPayment(it) }
    val jobOrderPaymentSummary = _date.switchMap { dailyReportRepository.jobOrderPaymentSummary(it) }

    val jobOrderWashItems = _date.switchMap { dailyReportRepository.jobOrderWashItems(it) }
    val jobOrderDryItems = _date.switchMap { dailyReportRepository.jobOrderDryItems(it) }
    val jobOrderExtrasItems = _date.switchMap { dailyReportRepository.jobOrderExtrasItems(it) }
    val jobOrderProductsItems = _date.switchMap { dailyReportRepository.jobOrderProductsItems(it) }
    val jobOrderDeliveryItems = _date.switchMap { dailyReportRepository.jobOrderDeliveryItems(it) }

    val machineUsageSummary = _date.switchMap { dailyReportRepository.machineUsageSummary(it) }

//    val washServices = _date.switchMap { dailyReportRepository.washServices(it) }
//    val dryServices = _date.switchMap { dailyReportRepository.dryServices(it) }
//    val washDrySummary = MediatorLiveData<DailyReportWashDrySummary>().apply {
//        fun update() {
//            val washes = washServices.value?.sumOf { it.count } ?: 0
//            val dries = dryServices.value?.sumOf { it.count } ?: 0
//            value = DailyReportWashDrySummary(
//                washes, dries, washes + dries
//            )
//        }
//        addSource(washServices) {update()}
//        addSource(dryServices) {update()}
//    }

//    val extras = _date.switchMap { dailyReportRepository.extras(it) }
//    val extrasSum = MediatorLiveData<Int>().apply {
//        addSource(extras) {
//            value = it.sumOf { it.count }
//        }
//    }

//    val detergents = _date.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.DETERGENT) }
//    val fabCons = _date.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.FAB_CON) }
//    val otherProducts = _date.switchMap { dailyReportRepository.productsChemicals(it, EnumProductType.OTHER) }
//    val productsChemicalsSummary = _date.switchMap { dailyReportRepository.productsChemicalsSummary(it) }

//    val delivery = _date.switchMap { dailyReportRepository.delivery(it) }
//    val deliverySum = MediatorLiveData<Int>().apply {
//        addSource(delivery) {
//            value = it.sumOf { it.count }
//        }
//    }

//    val machineUsages = _date.switchMap { dailyReportRepository.machineUsages(it) }
//    val machineUsageCount = MediatorLiveData<Int>().apply {
//        addSource(machineUsages) {
//            value = it.sumOf { it.count }
//        }
//    }

    val expenses = _date.switchMap { dailyReportRepository.expenses(it) }
    val expensesTotal = MediatorLiveData<Float>().apply {
        addSource(expenses) {
            value = it.sumOf { it.amount }.toFloat()
        }
    }

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun openExportToExcelDialog() {
        _date.value?.let {
            _navigationState.value = NavigationState.OpenExportOptions(it)
        }
    }

    fun openJobOrders() {
        _date.value?.let {
            _navigationState.value = NavigationState.OpenJobOrders(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class OpenExportOptions(val date: LocalDate): NavigationState()
        data class OpenJobOrders(val date: LocalDate): NavigationState()
    }
}