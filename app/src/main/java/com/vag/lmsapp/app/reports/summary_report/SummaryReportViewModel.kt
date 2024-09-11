package com.vag.lmsapp.app.reports.summary_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.reports.summary_report.job_order_items.SummaryReportJobOrderItemDetails
import com.vag.lmsapp.app.reports.summary_report.machine_usage.SummaryReportMachineUsageSummary
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.room.repository.SummaryReportRepository
import com.vag.lmsapp.util.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryReportViewModel

@Inject
constructor(
    private val dailyReportRepository: SummaryReportRepository
): ViewModel(){
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

//    private val _date = MutableLiveData(LocalDate.now())

    private val _dateFilter = MutableLiveData<DateFilter>()
    val dateFilter: LiveData<DateFilter> = _dateFilter

    val jobOrder = _dateFilter.switchMap { dailyReportRepository.jobOrder(it) }

    val jobOrderPayment = _dateFilter.switchMap { dailyReportRepository.jobOrderPayment(it) }
    val jobOrderPaymentSummary = _dateFilter.switchMap { dailyReportRepository.jobOrderPaymentSummary(it) }
    val unpaidJobOrders = dailyReportRepository.unpaidJobOrders()

    val jobOrderItemDetails = MutableLiveData<List<SummaryReportJobOrderItemDetails>>()

    val jobOrderWashItems = _dateFilter.switchMap { dailyReportRepository.jobOrderServiceItems(it, EnumServiceType.WASH) }
    val jobOrderDryItems = _dateFilter.switchMap { dailyReportRepository.jobOrderServiceItems(it, EnumServiceType.DRY) }
    val jobOrderExtrasItems = _dateFilter.switchMap { dailyReportRepository.jobOrderExtrasItems(it) }
    val jobOrderProductsItems = _dateFilter.switchMap { dailyReportRepository.jobOrderProductsItems(it) }
    val jobOrderDeliveryItems = _dateFilter.switchMap { dailyReportRepository.jobOrderDeliveryItems(it) }

    val machineUsageSummary = _dateFilter.switchMap { dailyReportRepository.machineUsageSummary(it) }

    val expenses = _dateFilter.switchMap { dailyReportRepository.expenses(it) }
    val expensesTotal = MediatorLiveData<Float>().apply {
        addSource(expenses) {
            value = it.sumOf { it.amount }.toFloat()
        }
    }

    fun setDateFilter(dateFilter: DateFilter) {
//        _date.value = date
        _dateFilter.value = dateFilter
    }

    fun openExportToExcelDialog() {
        _dateFilter.value?.let {
            _navigationState.value = NavigationState.OpenExportOptions(it)
        }
    }

    fun openJobOrders() {
        _dateFilter.value?.let {
            _navigationState.value = NavigationState.OpenJobOrders(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun openPayments() {
        _dateFilter.value?.let {
            _navigationState.value = NavigationState.OpenPayments(it)
        }
    }

    fun openExpenses() {
        _dateFilter.value?.let {
            _navigationState.value = NavigationState.OpenExpenses(it)
        }
    }

    fun openMachineUsage(machineUsage: SummaryReportMachineUsageSummary) {
        val date = _dateFilter.value!!
        val machineType = machineUsage.machineType
        val serviceType = machineUsage.serviceType
        _navigationState.value = NavigationState.OpenMachineUsage(date, machineType, serviceType)
    }

    fun openServices(enumServiceType: EnumServiceType) {
        _dateFilter.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsServices(it, enumServiceType).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems(enumServiceType.value + " services")
                }
            }
        }
    }

    fun openExtras() {
        _dateFilter.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsExtras(it).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems("Extra services")
                }
            }
        }
    }

    fun openProducts() {
        _dateFilter.value?.let {
            viewModelScope.launch {
                dailyReportRepository.getJobOrderItemsDetailsProducts(it).let {
                    jobOrderItemDetails.value = it
                    _navigationState.value = NavigationState.OpenJobOrderItems("Products and chemicals")
                }
            }
        }
    }

    fun openDeliveries() {
        _dateFilter.value?.let {
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
        data class OpenExportOptions(val date: DateFilter): NavigationState()
        data class OpenJobOrders(val date: DateFilter): NavigationState()
        data class OpenPayments(val date: DateFilter): NavigationState()
        data class OpenExpenses(val date: DateFilter): NavigationState()
        data class OpenJobOrderItems(val title: String): NavigationState()
        data class OpenMachineUsage(val date: DateFilter, val machineType: EnumMachineType, val serviceType: EnumServiceType): NavigationState()
    }
}