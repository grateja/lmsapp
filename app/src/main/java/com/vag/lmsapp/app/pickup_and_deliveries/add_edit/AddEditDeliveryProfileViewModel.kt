package com.vag.lmsapp.app.pickup_and_deliveries.add_edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityDeliveryProfile
import com.vag.lmsapp.room.repository.DeliveryProfilesRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditDeliveryProfileViewModel
@Inject
constructor(
    private val repository: DeliveryProfilesRepository
): CreateViewModel<EntityDeliveryProfile>(repository) {
    val baseFare = MutableLiveData<String>()
    val pricePerKm = MutableLiveData<String>()

    fun get(id: UUID?) {
        viewModelScope.launch {
            super.get(id, EntityDeliveryProfile()).let {
                baseFare.value = it.baseFare.toString()
                pricePerKm.value = it.pricePerKm.toString()
            }
        }
    }

    fun validate() {
        val baseFare = baseFare.value
        val pricePerKm = pricePerKm.value

        val inputValidation = InputValidation()
        inputValidation.addRule("baseFare", baseFare, arrayOf(Rule.Required, Rule.IsNumeric))
        inputValidation.addRule("pricePerKm", pricePerKm, arrayOf(Rule.Required, Rule.IsNumeric))



        if(!super.isInvalid(inputValidation)) {
            model.value = model.value.apply {
                this?.baseFare = baseFare!!.toFloat()
                this?.pricePerKm = pricePerKm!!.toFloat()
            }
        }
    }
}