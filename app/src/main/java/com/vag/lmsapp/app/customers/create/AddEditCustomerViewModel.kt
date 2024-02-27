package com.vag.lmsapp.app.customers.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.CRUDActionEnum
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.repository.CustomerRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditCustomerViewModel
@Inject
constructor(
    private val repository: CustomerRepository
) : CreateViewModel<EntityCustomer>(repository)
{
    private var _showSearchButton = MutableLiveData<Boolean>()
    var showSearchButton: LiveData<Boolean> = _showSearchButton
    private var originalName: String? = null
    private var originalCRN: String? = null
    fun get(id: UUID?, presetName: String?) {
        model.value.let {
            if(it != null) return
            println("not returned")
            viewModelScope.launch {
                val crn = repository.getNextJONumber()
                super.get(id, EntityCustomer(crn, presetName)).let { customer ->
                    originalName = customer.name
                    originalCRN = customer.crn
                }
            }
        }
    }

    fun replace(id: UUID) {
        viewModelScope.launch {
            repository.get(id).let {
                model.value = it
                originalName = it?.name
                originalCRN = it?.crn
                if(it != null) {
                    crudActionEnum.value = CRUDActionEnum.UPDATE
                }
            }
        }
    }

    fun validate() {
        viewModelScope.launch {
            val customer = model.value
            val inputValidation = InputValidation().apply {
                addRule("name", customer?.name, arrayOf(Rule.Required))
                addRule("crn", customer?.crn, arrayOf(Rule.Required))

                if(originalName != customer?.name &&  repository.checkName(customer?.name)) {
                    addError("name", "Name already taken. Please specify more details")
                }

                if(originalCRN != customer?.crn) {
                    repository.getCustomerMinimalByCRN(customer?.crn)?.let { customer ->
                        addError("crn", "CRN Conflict with ${customer.name} : ${customer.crn}")
                    }
                }
            }
            super.isInvalid(inputValidation)
        }
    }

    fun presetCustomerName(name: String?) {

    }

    fun toggleSearchVisibility(value: Boolean) {
        _showSearchButton.value = value
    }

//    override fun save() {
//        model.value?.let {
//            val inputValidation = InputValidation()
//            inputValidation.addRule("name", it.name.toString(), arrayOf(Rule.Required))
//
//            viewModelScope.launch {
//                if(originalName != it.name &&  repository.checkName(it.name)) {
//                    inputValidation.addError("name", "Name already taken. Please specify more details")
//                }
//                if(originalCRN != it.crn) {
//                    repository.getCustomerMinimalByCRN(it.crn)?.let { customer ->
//                        inputValidation.addError("crn", "CRN Conflict with ${customer.name} : ${customer.crn}")
//                    }
//                }
//                validation.value = inputValidation
//                super.save()
////                if(inputValidation.isInvalid()) {
////                    validation.value = inputValidation
////                    return@launch
////                }
////                repository.save(it)?.let { customer ->
////                    model.value = customer
////                    dataState.value = DataState.Save(customer)
////                }
//            }
//        }
//    }
}