package com.vag.lmsapp.app.reports.daily_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.reports.daily_report.job_order_items.DailyReportJobOrderItemDetails
import com.vag.lmsapp.app.reports.daily_report.machine_usage.DailyReportMachineUsageSummary
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.repository.DailyReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    val jobOrderItemDetails = MutableLiveData<List<DailyReportJobOrderItemDetails>>()

    val jobOrderWashItems = _date.switchMap { dailyReportRepository.jobOrderServiceItems(it, EnumServiceType.WASH) }
    val jobOrderDryItems = _date.switchMap { dailyReportRepository.jobOrderServiceItems(it, EnumServiceType.DRY) }
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
        val serviceType = machineUsage.serviceType
        _navigationState.value = NavigationState.OpenMachineUsage(date, machineType, serviceType)
    }

    fun openServices(enumServiceType: EnumServiceType) {
        _date.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsServices(it, enumServiceType).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems(enumServiceType.value + " services")
                }
            }
        }
    }

    fun openExtras() {
        _date.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsExtras(it).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems("Extra services")
                }
            }
        }
    }

    fun openProducts() {
        _date.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsProducts(it).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems("Products and chemicals")
                }
            }
        }
    }

    fun openDeliveries() {
        _date.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsDeliveries(it).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems("Pickup and Deliveries")
                }
            }
        }
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class OpenExportOptions(val date: LocalDate): NavigationState()
        data class OpenJobOrders(val date: LocalDate): NavigationState()
        data class OpenPayments(val date: LocalDate): NavigationState()
        data class OpenExpenses(val date: LocalDate): NavigationState()
        data class OpenJobOrderItems(val title: String): NavigationState()
        data class OpenMachineUsage(val date: LocalDate, val machineType: EnumMachineType, val serviceType: EnumServiceType): NavigationState()
    }
}