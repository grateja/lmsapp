package com.vag.lmsapp.app.extras.edit

import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.model.Rule
import com.vag.lmsapp.room.entities.EntityExtras
import com.vag.lmsapp.room.repository.ExtrasRepository
import com.vag.lmsapp.util.InputValidation
import com.vag.lmsapp.viewmodels.CreateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExtrasAddEditViewModel

@Inject
constructor(
    private val repository: ExtrasRepository
) : CreateViewModel<EntityExtras>(repository) {
    val categories = repository.getCategories()

    fun get(id: UUID?) {
        viewModelScope.launch {
            super.get(id, EntityExtras())
        }
    }

    fun validate() {
        val inputValidation = InputValidation().apply {
            addRule("name", model.value?.name, arrayOf(Rule.Required))
            addRule("price", model.value?.price, arrayOf(Rule.Required, Rule.IsNumeric))
        }
        super.isInvalid(inputValidation)
    }
//
//    override fun save() {
//        this.validation.value = InputValidation().apply {
//            addRule("name", model.value?.name, arrayOf(Rule.Required))
//            addRule("price", model.value?.price, arrayOf(Rule.Required, Rule.IsNumeric))
//        }
//
//        super.save()
//    }
}