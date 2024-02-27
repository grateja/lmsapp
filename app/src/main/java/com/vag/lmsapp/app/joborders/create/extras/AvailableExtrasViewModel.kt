package com.vag.lmsapp.app.joborders.create.extras

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.room.repository.ExtrasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AvailableExtrasViewModel

@Inject
constructor(
    private val repository: ExtrasRepository
) : ViewModel() {
    val availableExtras = MutableLiveData<List<MenuExtrasItem>>()
    val dataState = MutableLiveData<DataState>()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            repository.getAll("").let {
                availableExtras.value = it
                it.forEach {  m ->
                    println("extra id")
                    println(m.extrasRefId)
                }
            }
        }
    }

    fun setPreSelectedServices(services: List<MenuExtrasItem>?) {
        services?.forEach { msi ->
            println("msi id")
            println(msi.extrasRefId)
            println(availableExtras.value?.size)
            availableExtras.value?.find { msi.extrasRefId == it.extrasRefId }?.apply {
                println("selected")
                this.joExtrasItemId = msi.joExtrasItemId
                this.selected = msi.deletedAt == null
                this.quantity = msi.quantity
                this.deletedAt = msi.deletedAt
            }
        }
    }

    fun putService(quantityModel: QuantityModel) {
        val service = availableExtras.value?.find { it.extrasRefId == quantityModel.id }?.apply {
            selected = true
            quantity = quantityModel.quantity
            deletedAt = null
        }
        dataState.value = DataState.UpdateService(service!!)
    }

    fun removeService(service: QuantityModel) {
        availableExtras.value?.find { it.extrasRefId == service.id }?.apply {
            if(this.joExtrasItemId != null) {
                this.deletedAt = Instant.now()
            }
            this.selected = false
            dataState.value = DataState.UpdateService(this)
        }
    }

    fun prepareSubmit() {
        val list = availableExtras.value?.filter { it.selected || it.deletedAt != null }
        list?.let {
            dataState.value = DataState.Submit(it)
        }
    }

    fun resetState() {
        dataState.value = DataState.StateLess
    }

    sealed class DataState {
        object StateLess: DataState()
        data class UpdateService(val extrasItem: MenuExtrasItem) : DataState()
        data class Submit(val selectedItems: List<MenuExtrasItem>) : DataState()
    }
}