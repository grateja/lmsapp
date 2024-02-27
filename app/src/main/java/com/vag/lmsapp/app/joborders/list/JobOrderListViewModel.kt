package com.vag.lmsapp.app.joborders.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.model.EnumJoFilterBy
import com.vag.lmsapp.model.JobOrderAdvancedFilter
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobOrderListViewModel

@Inject
constructor(
    private val jobOrderRepository: JobOrderRepository
) : ListViewModel<JobOrderListItem, JobOrderAdvancedFilter>() {
//    val paymentStatus = MutableLiveData(EnumPaymentStatus.ALL)
    val result = MutableLiveData(JobOrderResultSummary())
//    val hideDeleted = MutableLiveData(true)
    val customerId = MutableLiveData<UUID?>()
    val nonVoidOnly = MutableLiveData(true)
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState
//    val includeVoid = MutableLiveData(false)

//    private val _dateFilter = MutableLiveData<DateFilter?>()
//    val dateFilter: LiveData<DateFilter?> = _dateFilter
//    val filterBy = MutableLiveData<EnumJoFilterBy>()

    fun setFilterBy(filterBy: EnumJoFilterBy) {
        filterParams.value = filterParams.value?.apply {
            this.filterBy = filterBy
        } ?: JobOrderAdvancedFilter(filterBy = filterBy)
    }

    override fun filter(reset: Boolean) {
        println("filtering $reset")
        job?.cancel()

        job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page.value = 1
            }

            val filterParams = filterParams.value ?: JobOrderAdvancedFilter()

            println("filter params")
            println(filterParams)

            val keyword = keyword.value
//            val orderBy = filterParams?.orderBy
//            val sortDirection = filterParams?.sortDirection
//            val paymentStatus = filterParams?.paymentStatus
//            val filterBy = filterParams?.filterBy ?: EnumJoFilterBy.DATE_CREATED
//            val includeVoid = filterParams?.includeVoid ?: false
//            val dateFilter = filterParams?.dateFilter

            val page = page.value ?: 1
            val customerId = customerId.value
            val nonVoidOnly = nonVoidOnly.value ?: true

            val result = jobOrderRepository.load(keyword, filterParams, page, customerId, nonVoidOnly)

            this@JobOrderListViewModel.result.value = result.count

            _dataState.value = DataState.LoadItems(result.items, reset)
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
    }

    fun setCustomerId(customerId: UUID?) {
        this.customerId.value = customerId
    }

//    fun setDateRange(dateFilter: DateFilter?) {
//        filterParams.value = filterParams.value?.apply {
//            this.dateFilter = dateFilter
//        } ?: JobOrderAdvancedFilter().apply {
//            this.dateFilter = dateFilter
//        }
//
////        filter(true)
//    }

//    fun showDatePicker() {
//        _dateFilter.value.let {
//            val dateFilter = it ?: DateFilter(LocalDate.now(), null)
//            _navigationState.value = NavigationState.OpenDateFilter(dateFilter)
//        }
//    }

//    fun showAdvancedFilter() {
//        val orderBy = orderBy.value ?: "Date Created"
//        val sortDirection = sortDirection.value ?: EnumSortDirection.DESC
//        val filterBy = filterBy.value
//        val includeVoid = includeVoid.value ?: false
//
//        val dateFilter = _dateFilter.value
//        filterParams.value ?: JobOrderAdvancedFilter(dateFilter, filterBy, includeVoid).let {
//            _navigationState.value = NavigationState.ShowAdvancedFilter(it)
//        }
//    }

    override fun clearState() {
        _navigationState.value = NavigationState.StateLess
        super.clearState()
    }

//    fun clearDates() {
//        _dateFilter.value = null
//    }

    fun setAdvancedFilter(advancedFilter: JobOrderAdvancedFilter) {
        filterParams.value = advancedFilter
    }

    fun showAdvancedFilter() {
        filterParams.value.let {
            _navigationState.value = NavigationState.ShowAdvancedFilter(
                it ?: JobOrderAdvancedFilter()
            )
            println("date range")
            println(it?.dateFilter)
        }
    }

    fun setView(nonVoidOnly: Boolean) {
        this.nonVoidOnly.value = nonVoidOnly
        filter(true)
    }

    sealed class NavigationState {
        object StateLess: NavigationState()
        data class OpenDateFilter(val dateFilter: DateFilter): NavigationState()
        data class ShowAdvancedFilter(val advancedFilter: JobOrderAdvancedFilter): NavigationState()
    }
}