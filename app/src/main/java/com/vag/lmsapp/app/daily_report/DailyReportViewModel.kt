package com.vag.lmsapp.app.daily_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.vag.lmsapp.app.daily_report.machine_usage.DailyReportMachineUsageSummary
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
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

    val jobOrder = _date.switchMap { dailyReportRepository.jobOrder(it) }

    val jobOrderPayment = _date.switchMap { dailyReportRepository.jobOrderPayment(it) }
    val jobOrderPaymentSummary = _date.switchMap { dailyReportRepository.jobOrderPaymentSummary(it) }
    val unpaidJobOrders = dailyReportRepository.unpaidJobOrders()

    val jobOrderWashItems = _date.switchMap { dailyReportRepository.jobOrderWashItems(it) }
    val jobOrderDryItems = _date.switchMap { dailyReportRepository.jobOrderDryItems(it) }
    val jobOrderExtrasItems = _date.switchMap { dailyReportRepository.jobOrderExtrasItems(it) }
    val jobOrderProductsItems = _date.switchMap { dailyReportRepository.jobOrderProductsItems(it) }
    val jobOrderDeliveryItems = _date.switchMap { dailyReportRepository.jobOrderDeliveryItems(it) }

    val machineUsageSummary = _date.switchMap { dailyReportRepository.machineUsageSummary(it) }

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

    fun openPayments() {
        _date.value?.let {
            _navigationState.value = NavigationState.OpenPayments(it)
        }
    }

    fun openExpenses() {
        _date.value?.let {
            _navigationState.value = NavigationState.OpenExpenses(it)
        }
    }

    fun openMachineUsage(machineUsage: DailyReportMachineUsageSummary) {
        val date = _date.value!!
        val machineType = machineUsage.machineType
        _navigationState.value = NavigationState.OpenMachineUsage(date, machineType)
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class OpenExportOptions(val date: LocalDate): NavigationState()
        data class OpenJobOrders(val date: LocalDate): NavigationState()
        data class OpenPayments(val date: LocalDate): NavigationState()
        data class OpenExpenses(val date: LocalDate): NavigationState()
        data class OpenMachineUsage(val date: LocalDate, val machineType: EnumMachineType): NavigationState()
    }
}