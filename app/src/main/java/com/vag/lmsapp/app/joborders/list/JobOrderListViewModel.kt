package com.vag.lmsapp.app.joborders.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.app.joborders.list.advanced_filter.JobOrderListAdvancedFilter
import com.vag.lmsapp.room.repository.JobOrderRepository
import com.vag.lmsapp.util.FilterState
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
) : ListViewModel<JobOrderListItem, JobOrderListAdvancedFilter>() {
    val result = MutableLiveData(JobOrderResultSummary())
    val customerId = MutableLiveData<UUID?>()
    val nonVoidOnly = MutableLiveData(true)

    override fun filter(reset: Boolean) {
        job?.cancel()

        job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page = 1
            }

            val filterParams = filterParams.value ?: JobOrderListAdvancedFilter()
            val keyword = keyword.value
            val customerId = customerId.value
            val nonVoidOnly = nonVoidOnly.value ?: true

            jobOrderRepository.load(keyword, filterParams, page, customerId, nonVoidOnly).let {
                setResult(it.items, it.resultCount, reset)
                result.value = it.count
            }
        }
    }

    fun setCustomerId(customerId: UUID?) {
        this.customerId.value = customerId
    }

    fun setAdvancedFilter(advancedFilter: JobOrderListAdvancedFilter) {
        filterParams.value = advancedFilter
    }

    fun showAdvancedFilter() {
        filterParams.value.let {
            setFilterState(FilterState.ShowAdvancedFilter(
                it ?: JobOrderListAdvancedFilter()
            ))
        }
    }

//    fun setView(nonVoidOnly: Boolean) {
//        this.nonVoidOnly.value = nonVoidOnly
//        filter(true)
//    }

    fun setPaymentStatus(status: EnumPaymentStatus) {
        filterParams.value = filterParams.value?.apply{
            paymentStatus = status
        } ?: JobOrderListAdvancedFilter(
            paymentStatus = status
        )
    }
}