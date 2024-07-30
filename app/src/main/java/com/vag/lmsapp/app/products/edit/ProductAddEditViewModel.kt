package com.vag.lmsapp.app.products.edit

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityProduct
import com.vag.lmsapp.room.repository.ProductRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductAddEditViewModel

@Inject
constructor(
    private val repository: ProductRepository
) : CreateViewModel<EntityProduct>(repository) {

//    val measureUnit = MutableLiveData(EnumMeasureUnit.PCS)

    fun get(id: UUID?) {
        viewModelScope.launch {
            val entity = super.get(id, EntityProduct().apply {
                unitPerServe = 1f
            })
//            measureUnit.value = entity.measureUnit
        }
    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("name", model.value?.name, arrayOf(Rule.Required))
            addRule("price", model.value?.price, arrayOf(Rule.Required, Rule.IsNumeric))
            addRule("unitPerServe", model.value?.unitPerServe, arrayOf(Rule.Required, Rule.IsNumeric))
        }
        super.isInvalid(inputValidation)
    }

    fun save(userId: UUID) {
        super.save()
    }

//    override fun save() {
//        val measureUnit = this.measureUnit.value
//
//        this.validation.value = InputValidation().apply {
//            addRule("remarks", model.value?.name, arrayOf(Rule.Required))
//            addRule("amount", model.value?.price, arrayOf(Rule.Required, Rule.IsNumeric))
////            addRules("tag", model.value?.tag, arrayOf(Rule.Required))
//        }
//        super.save()
//    }
}