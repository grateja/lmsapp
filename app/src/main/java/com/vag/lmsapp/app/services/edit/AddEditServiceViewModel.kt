package com.vag.lmsapp.app.services.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityService
import com.vag.lmsapp.room.entities.EntityServiceRef
import com.vag.lmsapp.room.repository.WashServiceRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditServiceViewModel

@Inject
constructor(
    private val repository: WashServiceRepository
) : CreateViewModel<EntityService>(repository) {
    val serviceType = MutableLiveData<EnumServiceType>()
    val machineType = MutableLiveData<EnumMachineType>()
    val washType = MutableLiveData<EnumWashType?>()
    val minutes = MutableLiveData<String>()
    val price = MutableLiveData<String>()

    fun get(serviceId: UUID?, enumMachineType: EnumMachineType) {
        viewModelScope.launch {
            super.get(serviceId, EntityService(enumMachineType)).let {
                machineType.value = it.serviceRef.machineType
                washType.value = it.serviceRef.washType
                minutes.value = it.serviceRef.minutes.toString()
                price.value = it.price.toString()

                println("machine type")
                println(it.serviceRef.machineType)
            }
        }
    }

    fun validate() {
        val service = model.value ?: return
        val machineType = machineType.value ?: return
        val washType = washType.value
        val minutes = minutes.value?.toIntOrNull()
        val price = price.value?.toFloatOrNull()

        val inputValidation = InputValidation()

        inputValidation.addRule(
            "name",
            service.name,
            arrayOf(Rule.Required)
        )

        inputValidation.addRule(
            "minutes",
            minutes,
            arrayOf(
                Rule.Required,
                Rule.IsNumeric
            )
        )

        inputValidation.addRule(
            "price",
            price,
            arrayOf(
                Rule.Required,
                Rule.IsNumeric
            )
        )

        if(machineType == EnumMachineType.REGULAR_WASHER || machineType == EnumMachineType.TITAN_WASHER) {
            inputValidation.addRule(
                "washType",
                washType,
                arrayOf(
                    Rule.Required
                )
            )
        } else {
            inputValidation.addRule(
                "minutes",
                minutes,
                arrayOf(
                    Rule.Required,
                    Rule.DivisibleBy10,
                    Rule.Min(10f)
                )
            )
        }

        super.isInvalid(inputValidation)
    }

    override fun save() {
        val serviceType = serviceType.value ?: EnumServiceType.OTHER
        val machineType = machineType.value ?: return
        val washType = washType.value
        val minutes = minutes.value?.toIntOrNull()
        val price = price.value?.toFloatOrNull()

        if(minutes == null || price == null) return

        model.value?.apply {
            serviceRef = EntityServiceRef(
                serviceType, machineType, washType, minutes
            )
            this.price = price
        }

        super.save()
    }

    fun setServiceType(serviceType: EnumServiceType) {
        this.serviceType.value = serviceType
    }

    fun setMachineType(machineType: EnumMachineType) {
        this.machineType.value = machineType
    }
}