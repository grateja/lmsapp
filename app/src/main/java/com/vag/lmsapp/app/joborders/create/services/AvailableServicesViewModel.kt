package com.vag.lmsapp.app.joborders.create.services

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.room.repository.WashServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AvailableServicesViewModel

@Inject
constructor(
    private val serviceRepository: WashServiceRepository
) : ViewModel() {
    val availableServices = MutableLiveData<List<MenuServiceItem>>()
    val dataState = MutableLiveData<DataState>()
    val selectedTab = MutableLiveData<EnumMachineType>()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            availableServices.value = serviceRepository.getAll()
        }
    }

    fun getServices(machineType: EnumMachineType) : List<MenuServiceItem> {
        return availableServices.value?.filter {it.machineType == machineType}?: listOf()
    }

    fun setPreSelectedServices(services: List<MenuServiceItem>?) {
        println("available services")
        println(availableServices.value)
        services?.forEach { msi ->
            println("msi id")
            println(msi.serviceRefId)
            availableServices.value?.find {
                println("sid")
                println(it.serviceRefId)
                msi.serviceRefId == it.serviceRefId
            }?.apply {
                this.joServiceItemId = msi.joServiceItemId
                this.selected = !msi.deleted
                this.quantity = msi.quantity
                this.used = msi.used
                this.deleted = msi.deleted
            }
        }
    }

    fun putService(quantityModel: QuantityModel) {
        val service = availableServices.value?.find { it.serviceRefId == quantityModel.id }?.apply {
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
        availableServices.value?.find { it.serviceRefId == service.id }?.apply {
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
        val list = availableServices.value?.filter { it.selected || it.deleted }
        list?.let { it ->
            dataState.value = DataState.Submit(it)
        }
    }

    fun resetState() {
        dataState.value = DataState.StateLess
    }

    fun setMachineType(text: String?) {
        println("machine type")
        println(text)
        selectedTab.value = EnumMachineType.fromName(text) ?: EnumMachineType.REGULAR_WASHER
    }

    sealed class DataState {
        object StateLess: DataState()
        data class UpdateService(val serviceItem: MenuServiceItem) : DataState()
        data class InvalidOperation(val message: String) : DataState()
        data class Submit(val selectedItems: List<MenuServiceItem>) : DataState()
    }
}