package com.vag.lmsapp.app.joborders.create.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.customers.CustomerMinimal
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.room.repository.JobOrderRepository
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
    private val jobOrderRepository: JobOrderRepository,
    private val joSettings: JobOrderSettingsRepository
) : ListViewModel<CustomerMinimal, BaseFilterParams>() {
    private val _navigationState = MutableLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _maxNumberOfUnpaidJobOrder = joSettings.maxUnpaidJobOrderLimit
    private var _currentCustomerId: UUID? = null
    private val _customerId = MutableLiveData<UUID>()
    val customer = _customerId.switchMap { repository.getCustomerAsLiveData(it) }
    val jobOrders = _customerId.switchMap { jobOrderRepository.getUnpaidJobOrdersAsLiveData(it) }
    val limit = MediatorLiveData<String>().apply {
        fun update() {
            val maxNumberOfUnpaidJobOrder = _maxNumberOfUnpaidJobOrder.value ?: 0
            val currentUnpaidJobOrder = jobOrders.value?.size ?: 0
            value = "$currentUnpaidJobOrder/$maxNumberOfUnpaidJobOrder"
        }
        addSource(jobOrders) {update()}
        addSource(_maxNumberOfUnpaidJobOrder){update()}
    }

    val canCreateNewJobOrder = MediatorLiveData<Boolean>().apply {
        fun update() {
            val maxNumberOfUnpaidJobOrder = _maxNumberOfUnpaidJobOrder.value ?: 0
            val currentUnpaidJobOrder = jobOrders.value?.size ?: 0
            value = currentUnpaidJobOrder < maxNumberOfUnpaidJobOrder
        }
        addSource(jobOrders) {update()}
        addSource(_maxNumberOfUnpaidJobOrder){update()}
    }

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
                _currentCustomerId
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
        _currentCustomerId = customerId
    }

    fun checkCustomer(customerId: UUID) {
        _customerId.value = customerId
    }

    fun selectCustomer() {
        _customerId.value?.let {
            _navigationState.value = NavigationState.SelectCustomer(it)
        }
    }

    fun resetState() {
        _navigationState.value = NavigationState.StateLess
    }

    fun editCustomer() {
        _customerId.value?.let {
            _navigationState.value = NavigationState.EditCustomer(it)
        }
    }

    fun selectJobOrder(jobOrderId: UUID) {
        _navigationState.value = NavigationState.SelectJobOrder(jobOrderId)
    }

    sealed class NavigationState {
        data object StateLess: NavigationState()
        data class SelectCustomer(val customerId: UUID): NavigationState()
        data class SelectJobOrder(val jobOrderId: UUID): NavigationState()
        data class EditCustomer(val customerId: UUID): NavigationState()
    }
}