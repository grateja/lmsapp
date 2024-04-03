package com.vag.lmsapp.app.joborders.create.delivery

import androidx.lifecycle.*
import com.vag.lmsapp.model.EnumDeliveryOption
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.util.DataState
import com.vag.lmsapp.util.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel

@Inject
constructor(
    private val repository: DeliveryProfilesRepository
) : ViewModel()
{
    var deliveryProfiles = MutableLiveData<List<MenuDeliveryProfile>>()
    val deliveryOption = MutableLiveData(EnumDeliveryOption.PICKUP_AND_DELIVERY)
    val distance = MutableLiveData(1)
    val profile = MutableLiveData<MenuDeliveryProfile>()

    val price = MediatorLiveData<Float>(0f).apply {
        fun update() {
            val baseFare = profile.value?.baseFare ?: 0f
            val pricePerKm = profile.value?.pricePerKm ?: 0f
            val distance = (distance.value ?: 0).toFloat()
            val option = deliveryOption.value?.charge ?: 0
            val minDistance = profile.value?.minDistance ?: 0f

//            value = ((distance * pricePerKm) + baseFare) * option
            value = if (distance > minDistance) {
                // Calculate additional charge for distance exceeding minDistance
                val additionalDistance = distance - minDistance
                val additionalCharge = additionalDistance * pricePerKm

                // Combine base fare, additional charge, and option charge
                (baseFare + additionalCharge) * option
            } else {
                // Charge only base fare if distance is less than minDistance
                baseFare * option
            }
        }
        addSource(distance) { update()}
        addSource(deliveryOption) { update() }
        addSource(profile) {update()}
    }

    private val _dataState = MutableLiveData<DataState<DeliveryCharge>>()
    val dataState: LiveData<DataState<DeliveryCharge>> = _dataState

    val label = MediatorLiveData<String>().apply {
        fun update() {
            value = profile.value?.vehicle.toString()
        }
        addSource(profile) {update()}
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    fun setDeliveryProfile(profileId: UUID) {
        this.profile.value = deliveryProfiles.value?.find { it.deliveryProfileRefId == profileId }
    }

    fun prepareDeliveryCharge(delete: Boolean) {
        val inputValidation = InputValidation()

        val option = this.deliveryOption.value
        val profile = this.profile.value

        inputValidation.addRule("distance", distance.value,
            arrayOf(
                Rule.Required,
                Rule.IsNumeric)
        )

        if(profile == null) {
            inputValidation.addError("profile", "Please select delivery profile")
        }

        if(option == null) {
            inputValidation.addError("option", "Please select delivery option")
        }

        if(inputValidation.isInvalid()) {
            _dataState.value = DataState.InvalidInput(inputValidation)
            return
        }

        val distance = this.distance.value?.toFloat()
        val price = price.value //option!!.charge * ((profile!!.pricePerKm * distance!!) + profile.baseFare)
        val deletedAt = if(delete) { Instant.now() } else { null }

        println("price")
        println(price)

        println("profile")
        println(profile)

        val deliveryCharge = DeliveryCharge(profile!!.deliveryProfileRefId, profile.vehicle, distance!!, option!!, price!!, deletedAt)
        this._dataState.value = DataState.SaveSuccess(deliveryCharge)
    }

    fun setDeliveryOption(deliveryOption: EnumDeliveryOption) {
        this.deliveryOption.value = deliveryOption
    }

    fun setDeliveryCharge(deliveryCharge: DeliveryCharge) {
        this.setDeliveryOption(deliveryCharge.deliveryOption)
        this.setDeliveryProfile(deliveryCharge.deliveryProfileId)
        this.distance.value = deliveryCharge.distance.toInt()
    }

    init {
        viewModelScope.launch {
            deliveryProfiles.value = repository.getAll()
        }
    }
}