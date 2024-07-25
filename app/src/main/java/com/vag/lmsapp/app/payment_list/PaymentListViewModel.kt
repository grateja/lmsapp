package com.vag.lmsapp.app.payment_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.dashboard.data.DateFilter
import com.vag.lmsapp.app.payment_list.advanced_filter.JobOrderPaymentAdvancedFilter
import com.vag.lmsapp.room.entities.EntityJobOrderPaymentListItem
import com.vag.lmsapp.room.repository.PaymentRepository
import com.vag.lmsapp.util.FilterState
import com.vag.lmsapp.util.ResultCount
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel

@Inject
constructor (
    private val repository: PaymentRepository
): ListViewModel<EntityJobOrderPaymentListItem, JobOrderPaymentAdvancedFilter>() {
    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }


        job = viewModelScope.launch {
            if(!keyword.value.isNullOrBlank()) {
                delay(500)
            } else {
                delay(100)
            }

            if(reset) {
                page = 1
            }

            repository.queryResult(keyword.value, page, filterParams.value).let {
                setResult(it.items, it.resultCount, reset)
            }
        }
    }

    fun showAdvancedFilter() {
        val filter = filterParams.value ?: JobOrderPaymentAdvancedFilter()
        setFilterState(FilterState.ShowAdvancedFilter(filter))
    }
}