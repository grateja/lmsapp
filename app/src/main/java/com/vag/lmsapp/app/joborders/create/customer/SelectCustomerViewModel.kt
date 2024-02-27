package com.vag.lmsapp.app.joborders.create.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.settings.JobOrderSettingsRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SelectCustomerViewModel

@Inject
constructor(
    private val repository: CustomerRepository,
    private val joSettings: JobOrderSettingsRepository
) : ListViewModel<CustomerMinimal, BaseFilterParams>() {
    val maxNumberOfUnpaidJobOrder = joSettings.maxUnpaidJobOrderLimit

    private val _customerId = MutableLiveData<UUID>()
    val customerId: LiveData<UUID> = _customerId
    override fun filter(reset: Boolean) {
        job?.let {
            it.cancel()
            loading.value = false
        }

        job = viewModelScope.launch {
            loading.value = true
            delay(500)

            if(reset) {
                page.value = 1
            }

            val filterParams = filterParams.value

            val keyword = keyword.value
            val page = page.value ?: 1

            val result = repository.getCustomersMinimal(
                keyword,
                page,
                _customerId.value
            )
            _dataState.value = DataState.LoadItems(
                result,
                reset
            )
            loading.value = false
        }
    }

    fun loadMore() {
        page.value = page.value?.plus(1)
        filter(false)
    }

    fun setCurrentCustomerId(customerId: UUID) {
        _customerId.value = customerId
    }

    fun checkIfCustomerCanCreateNewJobOrder(customer: CustomerMinimal) {
        val maxUnpaid = maxNumberOfUnpaidJobOrder.value ?: 0
        val currentUnpaid = customer.unpaid ?: 0

        if(currentUnpaid >= maxUnpaid) {
        }
    }
}