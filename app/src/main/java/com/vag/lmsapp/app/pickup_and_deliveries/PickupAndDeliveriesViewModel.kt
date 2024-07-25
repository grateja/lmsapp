package com.vag.lmsapp.app.pickup_and_deliveries

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.delivery.MenuDeliveryProfile
import com.vag.lmsapp.model.BaseFilterParams
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.viewmodels.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickupAndDeliveriesViewModel

@Inject
constructor(
    private val repository: DeliveryProfilesRepository
) : ListViewModel<MenuDeliveryProfile, BaseFilterParams>(){
    override fun filter(reset: Boolean) {
        viewModelScope.launch {
            repository.getAll().let {
//                _dataState.value = DataState.LoadItems(it, reset)
                setResult(it, null, reset)
            }
        }
    }
}