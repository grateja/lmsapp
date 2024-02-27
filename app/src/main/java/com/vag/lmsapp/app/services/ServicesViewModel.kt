package com.vag.lmsapp.app.services

import androidx.lifecycle.*
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.entities.EntityService
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel

@Inject
constructor(
    private val repository: WashServiceRepository
) : ViewModel() {
    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState
    val selectedTab = MutableLiveData(EnumMachineType.REGULAR_WASHER)
    val services: LiveData<List<EntityService>> = selectedTab.switchMap { tab -> repository.getByMachineTypeAsLiveData(tab) }

    fun selectTab(tab: String?) {
        selectedTab.value = EnumMachineType.fromName(tab) ?: EnumMachineType.REGULAR_WASHER
    }

    fun openAddEdit(serviceId: UUID?) {
        val machineType = selectedTab.value ?: EnumMachineType.REGULAR_WASHER
        _dataState.value = DataState.OpenAddEdit(serviceId, machineType)
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    sealed class DataState {
        object StateLess : DataState()
        class OpenAddEdit(val serviceId: UUID?, val machineType: EnumMachineType) : DataState()
    }
}