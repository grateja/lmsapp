package com.vag.lmsapp.app.dashboard

import androidx.lifecycle.*
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.EnumJoFilterBy
import com.vag.lmsapp.room.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel

@Inject
constructor(
    private val customerRepository: CustomerRepository,
    private val jobOrderRepository: JobOrderRepository,
    private val expensesRepository: ExpensesRepository,
    private val jobOrderServiceRepository: JobOrderServiceRepository,
    private val jobOrderProductRepository: JobOrderProductRepository,
    private val jobOrderExtrasRepository: JobOrderExtrasRepository,
    private val jobOrderPickupDeliveryRepository: JobOrderPickupDeliveryRepository,
    private val machinesRepository: MachineRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _dateFilter = MutableLiveData(DateFilter(LocalDate.now(), null))
    val dateFilter: LiveData<DateFilter> = _dateFilter

    val customerCount = _dateFilter.switchMap {
        println("date filter customer")
        println(it)
        customerRepository.getDashboard(it)
    }
    val jobOrderCount = _dateFilter.switchMap { jobOrderRepository.getDashboard(it) }
    val expenses = _dateFilter.switchMap { expensesRepository.getDashboard(it) }
    val jobOrderServices = _dateFilter.switchMap { jobOrderServiceRepository.getDashboard(it) }
    val jobOrderProducts = _dateFilter.switchMap { jobOrderProductRepository.getDashboard(it) }
    val jobOrderExtras = _dateFilter.switchMap { jobOrderExtrasRepository.getDashboard(it) }
    val jobOrderPickupDeliveries = _dateFilter.switchMap { jobOrderPickupDeliveryRepository.getDashboard(it) }
//    val machineUsage = _dateFilter.switchMap { machinesRepository.getDashboard(it) }
    val cashCollection = _dateFilter.switchMap { paymentRepository.cashCollection(it) }
    val cashlessPayments = _dateFilter.switchMap { paymentRepository.cashlessPayments(it) }
    val dateConflict = MediatorLiveData<String>().apply {
        fun update() {
            val dateFilter = dateFilter.value
            value = if (dateFilter?.dateTo != null) {
                if (dateFilter.dateFrom > dateFilter.dateTo) {
                    "Start date cannot be after end date"
                } else {
                    null // No conflict
                }
            } else {
                null // Handle the case where dateFilter or its properties are null
            }
        }
        addSource(dateFilter) {update()}
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun setStartDate(dateFrom: LocalDate) {
        _dateFilter.value = _dateFilter.value?.apply {
            this.dateFrom = dateFrom
        }
    }

    fun setEndDate(dateTo: LocalDate) {
        _dateFilter.value = _dateFilter.value?.apply {
            this.dateTo = dateTo
        }
    }

    fun openCustomers() {
        _dateFilter.value?.let {
            if(customerCount.value == 0) {
                _navigationState.value = NavigationState.Invalidate("No record")
                return
            }
            _navigationState.value = NavigationState.OpenCustomers(it)
        }
    }

    fun openJobOrders(filterBy: EnumJoFilterBy) {
        _dateFilter.value?.let {
            if(jobOrderCount.value?.total() == 0) {
                _navigationState.value = NavigationState.Invalidate("No record")
                return
            }
            _navigationState.value = NavigationState.OpenJobOrders(it, filterBy)
        }
    }

    fun openPayments() {
        _dateFilter.value?.let {
            val collections = cashCollection.value ?: 0
            val cashless = cashlessPayments.value?.size ?: 0
            println("cash less")
            println(cashless)
            println("cash")
            println(collections)

            if(cashless.plus(collections.toInt()) == 1) {
                _navigationState.value = NavigationState.Invalidate("No record")
                return
            }
            _navigationState.value = NavigationState.OpenJobOrdersPayments(it)
        }
    }

    fun openExpenses() {
        _dateFilter.value?.let {
            if(expenses.value?.size == 0) {
                _navigationState.value = NavigationState.Invalidate("No record")
                return
            }
            _navigationState.value = NavigationState.OpenExpenses(it)
        }
    }

    fun switchDates() {
        dateFilter.value?.let { filter ->
            filter.dateTo?.let { to ->
                val dateFrom = filter.dateFrom
                val switchedDateFilter = DateFilter(to, dateFrom)
                _dateFilter.value = switchedDateFilter
                _navigationState.value = NavigationState.SwitchDates(switchedDateFilter)
            }
        }
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateRangePicker(val dateFilter: DateFilter) : NavigationState()
        data class OpenCustomers(val dateFilter: DateFilter) : NavigationState()
        data class OpenJobOrders(val dateFilter: DateFilter, val filterBy: EnumJoFilterBy) : NavigationState()
        data class OpenJobOrdersPayments(val dateFilter: DateFilter) : NavigationState()
        data class OpenExpenses(val dateFilter: DateFilter) : NavigationState()
        data class SwitchDates(val dateFilter: DateFilter) : NavigationState()
        data class Invalidate(val message: String): NavigationState()
    }
}