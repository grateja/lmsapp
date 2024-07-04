package com.vag.lmsapp.app.joborders.create.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineTypeFilter
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailableServicesViewModel

@Inject
constructor(
    private val serviceRepository: WashServiceRepository
) : ViewModel() {
    private val _filter = MutableLiveData(
        MachineTypeFilter(
            EnumMachineType.REGULAR,
            EnumServiceType.WASH
        )
    )

    val filter: LiveData<MachineTypeFilter> = _filter

    private val _availableServices = MutableLiveData<List<MenuServiceItem>>()
    val availableServices = MediatorLiveData<List<MenuServiceItem>>().apply {
        fun update() {
            val filter = _filter.value
            value = _availableServices.value?.filter {
                it.serviceType == filter?.serviceType && it.machineType == filter.machineType && ((it.hidden && it.selected) || !it.hidden)
            } ?: emptyList()
        }
        addSource(_filter) {update()}
        addSource(_availableServices) {update()}
    }

    val dataState = MutableLiveData<DataState>()
//    val selectedTab = MutableLiveData<EnumMachineType>()

//    init {
//        loadServices()
//    }

    fun setMachineType(machineType: EnumMachineType?, serviceType: EnumServiceType?) {
        _filter.value = MachineTypeFilter(
            machineType, serviceType
        )
    }

//    private fun loadServices() {
//        viewModelScope.launch {
//            availableServices.value = serviceRepository.menuItems()
//        }
//    }

//    fun getServices(machineType: EnumMachineType) : List<MenuServiceItem> {
//        return availableServices.value?.filter {it.machineType == machineType}?: listOf()
//    }

    fun setPreSelectedServices(services: List<MenuServiceItem>?) {
        println("preset services")
        println(services)
        viewModelScope.launch {
            _availableServices.value = serviceRepository.menuItems().map {msi ->
                services?.find { it.serviceRefId == msi.serviceRefId }?.let {
                    println("found")
                    println(msi)
                    msi.joServiceItemId = it.joServiceItemId
                    msi.selected = !it.deleted
                    msi.quantity = it.quantity
                    msi.used = it.used
                    msi.deleted = it.deleted
                }
                msi
            }
        }
//        services?.forEach { msi ->
//            availableServices.value?.find {
//                msi.serviceRefId == it.serviceRefId
//            }?.apply {
//                this.joServiceItemId = msi.joServiceItemId
//                this.selected = !msi.deleted
//                this.quantity = msi.quantity
//                this.used = msi.used
//                this.deleted = msi.deleted
//            }
//        }
    }

    fun putService(quantityModel: QuantityModel) {
        val service = _availableServices.value?.find { it.serviceRefId == quantityModel.id }?.apply {
            println("used")
            println(used)

            println("quantity")
            println(quantityModel.quantity)
            if(quantityModel.quantity < used) {
                dataState.value = DataState.InvalidOperation("Cannot remove used service")
                return
            }
            selected = true
            quantity = quantityModel.quantity.toInt()
            deleted = false
        }
        dataState.value = DataState.UpdateService(service!!)
    }

    fun removeService(service: QuantityModel) {
        _availableServices.value?.find { it.serviceRefId == service.id }?.apply {
            if(this.joServiceItemId != null) {
                // It's already in the database
                if(this.used > 0) {
                    dataState.value = DataState.InvalidOperation("Cannot remove used service")
                    return
                }
                // Just mark deleted
                this.deleted = true
            }
            this.selected = false
//            this.quantity = 1
            dataState.value = DataState.UpdateService(this)
        }
    }

    fun prepareSubmit() {
        val list = _availableServices.value?.filter { it.selected || it.deleted }
        list?.let { it ->
            dataState.value = DataState.Submit(it)
        }
    }

    fun resetState() {
        dataState.value = DataState.StateLess
    }

//    fun setMachineType(text: String?) {
//        println("machine type")
//        println(text)
//        selectedTab.value = EnumMachineType.fromName(text) ?: EnumMachineType.REGULAR_WASHER
//    }

    sealed class DataState {
        object StateLess: DataState()
        data class UpdateService(val serviceItem: MenuServiceItem) : DataState()
        data class InvalidOperation(val message: String) : DataState()
        data class Submit(val selectedItems: List<MenuServiceItem>) : DataState()
    }
}