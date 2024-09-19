package com.vag.lmsapp.app.lms_live.sync

import androidx.lifecycle.ViewModel
import com.vag.lmsapp.network.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel

@Inject
constructor(
    private val repository: NetworkRepository
): ViewModel(){
    val pending = repository.getAllCounts()
    val customerCount = repository.customerCountAsLiveData()
    val jobOrderCount = repository.jobOrderCountAsLiveData()
    val machineUsageCount = repository.machineUsageCountAsLiveData()
    val inventoryLogCount = repository.inventoryLogCountAsLiveData()
    val jobOrderPaymentCount = repository.jobPaymentCountAsLiveData()
    val expensesCount = repository.expensesCountAsLiveData()
}